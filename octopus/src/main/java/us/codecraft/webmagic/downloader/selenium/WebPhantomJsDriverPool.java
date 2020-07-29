//package us.codecraft.webmagic.downloader.selenium;
//
///**
// * Created by chenshengju on 2017/9/20 0020.
// */
//
//import com.crawler.industry.config.CrawlerConfig;
//import com.crawler.proxyutils.XdailiUtils;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.phantomjs.PhantomJSDriver;
//import org.openqa.selenium.phantomjs.PhantomJSDriverService;
//import org.openqa.selenium.remote.DesiredCapabilities;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import us.codecraft.webmagic.proxy.Proxy;
//
//import java.io.File;
//import java.util.concurrent.BlockingDeque;
//import java.util.concurrent.LinkedBlockingDeque;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * @author taojw
// */
//public class WebPhantomJsDriverPool implements WebDriverPool{
//    private Logger logger = LoggerFactory.getLogger(getClass());
//    private int CAPACITY = 5;
//    private AtomicInteger refCount = new AtomicInteger(0);
//    private static final String DRIVER_PHANTOMJS = "phantomjs";
//
//    /**
//     * store webDrivers available
//     */
//    private BlockingDeque<WebDriver> innerQueue = new LinkedBlockingDeque<WebDriver>(
//            CAPACITY);
//    /**
//     * 默认驱动是在当前目录中
//     */
//    private static String PHANTOMJS_PATH;
//    private static DesiredCapabilities caps = DesiredCapabilities.phantomjs();
//
//    static {
////        String[] phantomArgs = new  String[] {
//////                "--webdriver-loglevel=INFO",
////                "--webdriver-loglevel=NONE",
//////                "--webdriver-loglevel=DEBUG",
////////                ,"--load-images=no"
////                "--disk-cache=true",
//////                "--disk-cache=false",
////                "--disk-cache-path=cache"
//////                "--offline-storage-path=d:/hh"
////        };
//        String[] phantomArgs = PhantomjsConfig.phantomArgs;
////        caps.setCapability("phantomjs.page.settings.loadImages",false);
//        //判断当前系统是windos还是linux
//        String os = System.getProperty("os.name");
//        if(os.toLowerCase().startsWith("win")){
//            PHANTOMJS_PATH = new File("resources/phantomjs.exe").getAbsolutePath();
//        }else
//        {
//            PHANTOMJS_PATH = new File("resources/phantomjs").getAbsolutePath();
//        }
//
//        caps.setJavascriptEnabled(true);
//        caps.setCapability(
//                PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
//                PHANTOMJS_PATH);
//        caps.setCapability("takesScreenshot", false);
//        caps.setCapability(
//                PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX
//                        + "User-Agent",
//                "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
//
////        caps.setCapability(
////                PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX
////                        + "Proxy-Authorization",
////                XdailiUtils.getProxyHeader());
//        caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);
//
//
//    }
//
//    public WebPhantomJsDriverPool() {
//    }
//
//    public  WebPhantomJsDriverPool(int poolsize) {
//        this.CAPACITY = poolsize;
//        innerQueue = new LinkedBlockingDeque<WebDriver>(poolsize);
//    }
//
//    public  WebDriver get() throws InterruptedException {
//        WebDriver poll = innerQueue.poll();
//        if (poll != null) {
//            return poll;
//        }
//        if (refCount.get() < CAPACITY) {
//            synchronized (innerQueue) {
//                if (refCount.get() < CAPACITY) {
//
//                    PhantomJSDriver mDriver = new PhantomJSDriver(caps);
////                    mDriver.executePhantomJS("phantom.setProxy('139.196.172.79:80')");
//                    mDriver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);
//                    mDriver.manage().timeouts().pageLoadTimeout(100,TimeUnit.SECONDS);
//                    mDriver.manage().timeouts().setScriptTimeout(10,TimeUnit.SECONDS);
//                    mDriver.manage().window().maximize();
//                    // mDriver.manage().window().setSize(new Dimension(1366,
//                    // 768));
//                    innerQueue.add(mDriver);
//                    refCount.incrementAndGet();
//                }
//            }
//        }
//        return innerQueue.take();
//    }
//
//    public void returnToPool(WebDriver webDriver) {
//        // webDriver.quit();
//        // webDriver=null;
//        innerQueue.add(webDriver);
//    }
//
//    public void close(WebDriver webDriver) {
//        refCount.decrementAndGet();
//        webDriver.close();
//        webDriver.quit();
//        webDriver = null;
//    }
//
//    public void shutdown() {
//        try {
//            for (WebDriver driver : innerQueue) {
//                close(driver);
//            }
//            innerQueue.clear();
//        } catch (Exception e) {
////            e.printStackTrace();
//            logger.error("webdriverpool关闭失败", e);
//        }
//    }
//
//    @Override
//    public void setProxy(WebDriver webDriver, Proxy proxy) {
//        PhantomJSDriver.class.cast(webDriver).executePhantomJS("phantom.setProxy('"+proxy.getHost()+"', "+proxy.getPort()+")");
//    }
//
//}
