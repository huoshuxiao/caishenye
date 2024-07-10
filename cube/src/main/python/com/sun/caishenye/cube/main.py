from com.sun.caishenye.cube.config import config, log
from com.sun.caishenye.cube.stock import money_more, boss


def main():
    log.log(r'base directory :: {}'.format(config.get('file.path')))
    money_more.run()
    boss.run()


if __name__ == '__main__':
    main()
