from com.sun.caishenye.cube.config import config
from com.sun.caishenye.cube.config import log
from com.sun.caishenye.cube.stock import money_more


def main():
    log.log(r'base directory :: {}'.format(config.get('file.path')))
    money_more.run()


if __name__ == '__main__':
    main()

