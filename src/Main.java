public class Main {
    public static void main(String[] args) {

        try {
            // 初始化 SQLite
            DB.init();
            System.out.println("DB initialized");

            // 启动手动补单系统（独立线程）
            new Thread(() -> ManualTradeCLI.start()).start();

            // 保持程序运行
            keepAlive();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void keepAlive() {
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}