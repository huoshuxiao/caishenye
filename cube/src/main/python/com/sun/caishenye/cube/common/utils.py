import csv
import datetime
import os

import pandas


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


def write_excel(file_path, file_name, df):
    df.to_excel(r'{}/{}.xlsx'.format(file_path, file_name), header=False, index=False, sheet_name=file_name)


def today():
    return datetime.date.today()


def year():
    return r'{}'.format(today().year)

