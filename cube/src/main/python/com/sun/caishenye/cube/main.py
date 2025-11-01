import os
from concurrent import futures

from com.sun.caishenye.cube.config import config, log
from com.sun.caishenye.cube.fund import annual_increase
from com.sun.caishenye.cube.stock import annual_increase as stock_annual_increase
from com.sun.caishenye.cube.stock.daily import money_flow, ten_holder
from com.sun.caishenye.cube.stock.month import boss, money_more


def main():
    log.log(r'base directory :: {}'.format(config.get('file.path')))

    thread_count = os.cpu_count() + 1
    task_funcs = [run_fund, run_stock, run_stock2]

    # 如果任务是I/O密集型的，即主要涉及网络通信、文件读写和数据库操作等，应该选择线程池，以充分利用线程的非阻塞特性，提高执行效率。
    with futures.ThreadPoolExecutor(max_workers=thread_count) as executor:
        fs = {executor.submit(fn): fn.__name__ for fn in task_funcs}
        for f in futures.as_completed(fs):
            name = fs[f]
            try:
                result = f.result()
                log.log(f"✅ << {name} >> completed.")
            except Exception as e:
                log.log(f"❌ {name} failed: {e}")


def run_fund():
    annual_increase.run()


def run_stock():
    money_more.run()
    boss.run()

    money_flow.run()
    ten_holder.run()


def run_stock2():
    stock_annual_increase.run()


if __name__ == '__main__':
    main()
