import csv
import datetime
import os

import pandas
import pandas as pd


def root():
    r"""get main directory ."""
    return os.path.dirname(os.path.abspath(__file__ + r'../../../../../../..'))


def resources_path():
    r"""get resources directory ."""
    return root() + r'/resources'


def read_csv(file_path, file_name, headers):
    file = r'{}/{}'.format(file_path, file_name)
    return pandas.read_csv(file, header=None, names=headers)


def write_csv(file_path, file_name, file_fields, body):
    # name of csv file
    file = r'{}/{}'.format(file_path, file_name)

    # field names
    fields = file_fields

    with open(file, 'w', encoding="utf-8", newline='') as csvfile:
        writer = csv.DictWriter(csvfile, fields)
        writer.writeheader()
        writer.writerows(body)


def write_excel(file_path, file_name, sheet_name, df):
    file = r'{}/{}.xlsx'.format(file_path, file_name)
    if not os.path.exists(file):
        df.to_excel(file, header=False, index=False, sheet_name=sheet_name)

    with pd.ExcelWriter(file, mode='a', if_sheet_exists='replace', engine='openpyxl') as writer:
        df.to_excel(writer, header=False, index=False, sheet_name=sheet_name)


def remove_file(file_path, file_name):
    file = r'{}/{}.xlsx'.format(file_path, file_name)
    if os.path.exists(file):
        os.remove(file)


def today():
    return datetime.date.today()


def year():
    return today().year


def is_work_day(target_day):
    _year = target_day.year
    _month = target_day.month
    _day = target_day.day
    date = datetime.date(_year, _month, _day)

    if (date.weekday() == 5
            or date.weekday() == 6):
        return False
    return True
