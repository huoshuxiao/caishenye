from com.sun.caishenye.cube.config import config
from com.sun.caishenye.cube.config import log
from com.sun.caishenye.cube.stock import money_more


def main():
    file_path = config.get('file.path')
    log.logger.debug(r'config :: {}'.format(file_path))

    money_more.y_current()


if __name__ == '__main__':
    main()

