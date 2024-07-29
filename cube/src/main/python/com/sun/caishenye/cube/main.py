from com.sun.caishenye.cube.config import config, log
from com.sun.caishenye.cube.stock import money_more, boss, money_flow, ten_holder


def main():
    log.log(r'base directory :: {}'.format(config.get('file.path')))
    money_more.run()
    boss.run()

    money_flow.run()
    ten_holder.run()


if __name__ == '__main__':
    main()
