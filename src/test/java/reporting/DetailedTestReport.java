package reporting;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

public class DetailedTestReport implements ITestListener, IReporter {
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("\nSTART: " + title(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        printResult("PASS", result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        printResult("FAIL", result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        printResult("SKIP", result);
    }

    private void printResult(String status, ITestResult result) {
        System.out.println(status + ": " + title(result) + " (" + duration(result) + " s)");
        if (result.getThrowable() != null) {
            System.out.println("Lý do: " + result.getThrowable());
        }
    }

    // TestNG gọi sau khi chạy xong để tự sinh báo cáo HTML.
    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
            String outputDirectory) {
        List<ITestResult> tests = new ArrayList<>();
        List<ITestResult> configurationFailures = new ArrayList<>();
        for (ISuite suite : suites) {
            for (ISuiteResult suiteResult : suite.getResults().values()) {
                ITestContext context = suiteResult.getTestContext();
                tests.addAll(context.getPassedTests().getAllResults());
                tests.addAll(context.getFailedTests().getAllResults());
                tests.addAll(context.getSkippedTests().getAllResults());
                tests.addAll(context.getFailedButWithinSuccessPercentageTests().getAllResults());
                configurationFailures.addAll(context.getFailedConfigurations().getAllResults());
            }
        }
        tests.sort(Comparator.comparing(DetailedTestReport::title));
        long passed = tests.stream().filter(r -> r.getStatus() == ITestResult.SUCCESS).count();
        long skipped = tests.stream().filter(r -> r.getStatus() == ITestResult.SKIP).count();
        long failed = tests.size() - passed - skipped;
        StringBuilder html = new StringBuilder(HEADER);
        html.append("<h1>Kết quả kiểm thử</h1><p class='muted'>Tạo lúc: ")
                .append(escape(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss z")
                        .withZone(ZoneId.systemDefault()).format(Instant.now())))
                .append("</p><div class='totals'>")
                .append("<span>Tổng: <b>").append(tests.size()).append("</b></span>")
                .append("<span class='PASS'>PASS: <b>").append(passed).append("</b></span>")
                .append("<span class='FAIL'>FAIL: <b>").append(failed).append("</b></span>")
                .append("<span class='SKIP'>SKIP: <b>").append(skipped).append("</b></span></div>");
        html.append(CHARTS);
        if (!configurationFailures.isEmpty()) {
            html.append("<section class='setup-error'><h2>Lỗi khởi tạo hoặc dọn dẹp: ")
                    .append(configurationFailures.size()).append("</h2>")
                    .append("<p>Kiểm tra phần này khi test bị SKIP do cấu hình thất bại.</p>");
            for (ITestResult result : configurationFailures) {
                html.append("<h3>").append(escape(result.getTestClass().getName()
                        + "." + result.getMethod().getMethodName())).append("</h3>");
                appendThrowable(html, result.getThrowable());
            }
            html.append("</section>");
        }
        html.append("""
                <div class='filters'>
                  <label>Tìm case <input id='search' type='search' placeholder='Tên case hoặc nội dung kiểm tra'></label>
                  <label>Trạng thái <select id='status'><option value='ALL'>Tất cả</option>
                    <option>PASS</option><option>FAIL</option><option>SKIP</option></select></label>
                  <span id='visible' aria-live='polite'></span>
                </div>
                <p class='muted'>Bấm tên case để xem từng bước và giá trị mong đợi/thực tế.
                Thời gian mỗi case tính phần @Test, không gồm @BeforeMethod.</p>
                """);
        for (ITestResult result : tests) {
            appendTest(html, result);
        }
        if (tests.isEmpty()) {
            html.append("<p>Không có test nào được chạy.</p>");
        }
        html.append(FOOTER);
        Path report = Path.of(outputDirectory, "index.html");
        try {
            Files.createDirectories(report.getParent());
            Files.writeString(report, html, StandardCharsets.UTF_8);
            System.out.println("Báo cáo chi tiết: " + report.toAbsolutePath());
        } catch (IOException error) {
            throw new IllegalStateException("Không ghi được báo cáo: " + report, error);
        }
    }

    private static void appendTest(StringBuilder html, ITestResult result) {
        String status = result.getStatus() == ITestResult.SUCCESS ? "PASS"
                : result.getStatus() == ITestResult.SKIP ? "SKIP" : "FAIL";
        html.append("<details class='test' data-status='").append(status).append("'")
                .append("FAIL".equals(status) ? " open" : "")
                .append("><summary><strong class='").append(status).append("'>")
                .append(status).append("</strong><span>").append(escape(title(result)))
                .append("</span><time>").append(duration(result)).append(" s</time></summary>")
                .append("<div class='body'><p class='muted'>")
                .append(escape(result.getTestClass().getName() + "."
                        + result.getMethod().getMethodName())).append("</p>");
        List<TestLog.Check> checks = TestLog.checksFor(result);
        if (!checks.isEmpty()) {
            html.append("<table><thead><tr><th>Trạng thái</th><th>Bước / kiểm tra</th>")
                    .append("<th>Mong đợi</th><th>Thực tế</th></tr></thead><tbody>");
            for (TestLog.Check check : checks) {
                html.append("<tr><td class='").append(escape(check.status())).append("'>")
                        .append(escape(check.status())).append("</td><td>")
                        .append(escape(check.label())).append("</td><td>")
                        .append(escape(check.expected())).append("</td><td>")
                        .append(escape(check.actual())).append("</td></tr>");
            }
            html.append("</tbody></table>");
        } else {
            html.append("<p>Chưa có log kiểm tra. Case có thể bị SKIP trước khi chạy,")
                    .append(" hoặc chưa dùng TestLog để ghi các bước.</p>");
        }
        appendThrowable(html, result.getThrowable());
        html.append("</div></details>");
    }

    private static void appendThrowable(StringBuilder html, Throwable error) {
        if (error == null) return;
        StringWriter trace = new StringWriter();
        error.printStackTrace(new PrintWriter(trace));
        html.append("<h4>Lý do / stack trace</h4><pre>")
                .append(escape(trace.toString())).append("</pre>");
    }

    private static String title(ITestResult result) {
        Object customTitle = result.getAttribute("report.title");
        if (customTitle instanceof String name && !name.isBlank()) return name;
        String description = result.getMethod().getDescription();
        return description == null || description.isBlank()
                ? result.getMethod().getMethodName() : description;
    }

    private static String duration(ITestResult result) {
        return String.format(Locale.ROOT, "%.3f",
                Math.max(0, result.getEndMillis() - result.getStartMillis()) / 1000.0);
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static final String CHARTS = """
            <section class="charts" aria-label="Biểu đồ tổng quan">
              <figure class="chart-panel">
                <figcaption><h2>Tỷ lệ kết quả</h2>
                  <p class="muted">Tổng hợp toàn bộ lượt chạy. Bấm trạng thái để lọc danh sách case.</p></figcaption>
                <div class="outcomes">
                  <div id="outcome-chart" class="donut" role="img">
                    <div class="donut-center"><strong id="pass-rate">—</strong><span>tỷ lệ PASS</span></div>
                  </div>
                  <div class="legend">
                    <button type="button" data-filter="PASS"><i class="dot pass-dot" aria-hidden="true"></i>PASS <b id="pass-count">0</b></button>
                    <button type="button" data-filter="FAIL"><i class="dot fail-dot" aria-hidden="true"></i>FAIL <b id="fail-count">0</b></button>
                    <button type="button" data-filter="SKIP"><i class="dot skip-dot" aria-hidden="true"></i>SKIP <b id="skip-count">0</b></button>
                  </div>
                </div>
                <p id="outcome-note" class="muted chart-note"></p>
              </figure>
              <figure class="chart-panel">
                <figcaption><h2>Thời gian từng test</h2>
                  <p class="muted">Xếp từ lâu nhất đến nhanh nhất, chỉ tính phần @Test.</p></figcaption>
                <div id="duration-chart" class="duration-chart"></div>
              </figure>
            </section>
            """;

    private static final String HEADER = """
            <!doctype html><html lang="vi"><head><meta charset="utf-8">
            <meta name="viewport" content="width=device-width,initial-scale=1">
            <title>Báo cáo kiểm thử chi tiết</title><style>
            *{box-sizing:border-box}body{margin:0;background:#f5f7fa;color:#192333;
            font:15px/1.55 system-ui,-apple-system,sans-serif}main{max-width:1200px;margin:36px auto;padding:0 24px}
            h1{margin-bottom:0;font-size:30px}.muted{color:#566579;font-size:13px}
            .totals{display:flex;gap:28px;flex-wrap:wrap;padding:20px 0;border-bottom:1px solid #cbd5e1}
            .totals span{font-size:19px}.PASS{color:#11643c}.FAIL{color:#b42318}.SKIP{color:#8a4b00}.INFO{color:#365f93}
            .charts{display:grid;grid-template-columns:minmax(330px,0.9fr) minmax(0,1.4fr);gap:20px;margin:24px 0}
            .chart-panel{margin:0;padding:22px;background:white;border:1px solid #d7dee8;border-radius:10px}
            .chart-panel h2{font-size:18px;margin:0}.chart-panel figcaption p{margin:6px 0 20px}
            .outcomes{display:flex;align-items:center;gap:24px;justify-content:center}
            .donut{width:160px;height:160px;flex:0 0 160px;border-radius:50%;background:#e2e8f0;
            display:grid;place-items:center}.donut-center{width:116px;height:116px;border-radius:50%;background:white;
            display:flex;align-items:center;justify-content:center;flex-direction:column}
            .donut-center strong{font-size:29px;line-height:1.2}.donut-center span{font-size:12px;color:#566579}
            .legend{display:grid;gap:7px}.legend button{display:flex;align-items:center;gap:9px;min-width:115px;
            border:1px solid transparent;border-radius:6px;padding:8px;background:white;color:#192333;font:inherit;cursor:pointer}
            .legend button:hover{background:#f1f5f9;border-color:#d7dee8}.legend button:focus-visible{outline:2px solid #365f93}
            .legend b{margin-left:auto}.dot{width:10px;height:10px;border-radius:50%}
            .pass-dot{background:#16a34a}.fail-dot{background:#dc2626}.skip-dot{background:#f59e0b}
            .chart-note{text-align:center;margin:16px 0 0}.duration-chart{display:grid;gap:18px}
            .duration-row{display:grid;grid-template-columns:minmax(120px,1fr) minmax(70px,1.1fr) 65px;align-items:center;gap:12px}
            .duration-label{font-size:13px;line-height:1.4;overflow-wrap:anywhere}.duration-track{height:12px;background:#edf1f6;
            border-radius:6px;overflow:hidden}.duration-fill{display:block;height:100%;border-radius:6px}
            .duration-value{text-align:right;font-variant-numeric:tabular-nums;font-size:12px;color:#566579}
            @media(max-width:850px){.charts{grid-template-columns:1fr}}
            .filters{display:flex;gap:20px;align-items:end;flex-wrap:wrap;margin-top:24px}
            label{display:grid;gap:5px;font-weight:600}input,select{padding:9px;border:1px solid #aab5c4;
            border-radius:5px;background:white;font:inherit}input{width:min(390px,75vw)}
            details.test{margin:14px 0;background:white;border:1px solid #d7dee8;border-radius:7px}
            summary{cursor:pointer;padding:17px;display:flex;gap:18px;align-items:baseline}
            summary span{flex:1}summary time{white-space:nowrap;font-size:13px;color:#566579}
            .body{padding:0 18px 18px;overflow-x:auto}table{border-collapse:collapse;width:100%;table-layout:fixed}
            th,td{padding:10px;text-align:left;vertical-align:top;border-bottom:1px solid #dde3eb;
            overflow-wrap:anywhere;white-space:pre-wrap}th{background:#eef2f6}th:first-child{width:90px}
            pre{white-space:pre-wrap;overflow-wrap:anywhere;background:#fff2ef;border-left:3px solid #b42318;
            padding:14px;font-size:12px}.setup-error{padding:16px;border:1px solid #df9b93;background:#fff6f4;margin-top:20px}
            [hidden]{display:none!important}@media(max-width:600px){main{padding:0 12px}summary{gap:9px;font-size:13px}
            th,td{padding:7px;font-size:12px}th:first-child{width:65px}}
            </style></head><body><main>
            """;

    private static final String FOOTER = """
            </main><script>
            const search=document.getElementById('search');const status=document.getElementById('status');
            const tests=[...document.querySelectorAll('details.test')];
            const colors={PASS:'#16a34a',FAIL:'#dc2626',SKIP:'#f59e0b'};
            const counts={PASS:0,FAIL:0,SKIP:0};
            tests.forEach(test=>counts[test.dataset.status]++);
            const total=tests.length;const ring=document.getElementById('outcome-chart');
            const passAngle=total?counts.PASS/total*360:0;
            const failAngle=total?(counts.PASS+counts.FAIL)/total*360:0;
            if(total)ring.style.background=`conic-gradient(${colors.PASS} 0deg ${passAngle}deg,
              ${colors.FAIL} ${passAngle}deg ${failAngle}deg,${colors.SKIP} ${failAngle}deg 360deg)`;
            ring.setAttribute('aria-label',`Tổng ${total} test: ${counts.PASS} PASS, ${counts.FAIL} FAIL, ${counts.SKIP} SKIP`);
            document.getElementById('pass-rate').textContent=total?(counts.PASS/total*100).toFixed(1)+'%':'—';
            ['PASS','FAIL','SKIP'].forEach(key=>document.getElementById(key.toLowerCase()+'-count').textContent=counts[key]);
            document.getElementById('outcome-note').textContent=total?`${counts.PASS} / ${total} test đạt`:'Chưa có test nào được chạy';
            const timings=tests.map(test=>({label:test.querySelector('summary span').textContent,
              seconds:Number(test.querySelector('time').textContent.replace(' s','')),status:test.dataset.status}));
            timings.sort((a,b)=>b.seconds-a.seconds);
            const maximum=Math.max(0,...timings.map(test=>test.seconds));
            const durationChart=document.getElementById('duration-chart');
            timings.forEach(test=>{
              const row=document.createElement('div');row.className='duration-row';
              const label=document.createElement('span');label.className='duration-label';label.textContent=test.label;
              const track=document.createElement('span');track.className='duration-track';track.setAttribute('aria-hidden','true');
              const fill=document.createElement('span');fill.className='duration-fill';
              fill.style.width=(maximum?test.seconds/maximum*100:0)+'%';fill.style.background=colors[test.status];
              track.appendChild(fill);
              const value=document.createElement('span');value.className='duration-value';value.textContent=test.seconds.toFixed(3)+' s';
              row.append(label,track,value);durationChart.appendChild(row);
            });
            if(!total)durationChart.textContent='Chưa có dữ liệu thời gian.';
            function filter(){let visible=0;const query=search.value.toLocaleLowerCase('vi');
              tests.forEach(test=>{const match=(status.value==='ALL'||test.dataset.status===status.value)
                &&test.textContent.toLocaleLowerCase('vi').includes(query);test.hidden=!match;if(match)visible++;});
              document.getElementById('visible').textContent=visible+' / '+tests.length+' case';}
            search.addEventListener('input',filter);status.addEventListener('change',filter);filter();
            document.querySelectorAll('[data-filter]').forEach(button=>button.addEventListener('click',()=>{
              status.value=button.dataset.filter;filter();
            }));
            </script></body></html>
            """;
}
