import tarfile
import shutil
import urllib2
import argparse
import os

from contextlib import closing

FTP_MONTHLY_PACKAGES = 'ftp://guest:guest@ted.europa.eu/monthly-packages/'

def main():
    parser = argparse.ArgumentParser()

    parser.add_argument(
        '--start-date',
        help='Date in format YYYY-MM',
        required=True
    )

    parser.add_argument(
        '--end-date',
        help='Date in format YYYY-MM',
        required=True
    )

    parser.add_argument(
        '--save-dir',
        help='Directory for downloaded data',
        default='./data',
    )

    args = parser.parse_args().__dict__

    start_date = map(int, args.pop('start_date').split('-'))
    end_date = map(int, args.pop('end_date').split('-'))
    data_dir = args.pop('save_dir')

    if start_date[0] == end_date[0]:
        map(untar, [download(start_date[0], month, data_dir) for month in range(start_date[1], end_date[1] + 1)])
        return


    tars = []
    for month in range(start_date[1], 13):
        tars += [download(start_date[0], month, data_dir)]

    for year in range(start_date[0] + 1, end_date[0]):
        for month in  range(1, 13):
            tars += [download(year, month, data_dir)]

    for month in range(1, end_date[1] + 1):
        tars += [download(end_date[0], month, data_dir)]

    map(untar, tars)


def download(year, month, output_dirname):
    print('---- Downloading data for %02d-%d ----' % (month, year))

    filename = '%d-%02d.tar.gz' % (year, month)
    ftp_adress = os.path.join(FTP_MONTHLY_PACKAGES, str(year), filename)
    output_filename = os.path.join(output_dirname, filename)

    with closing(urllib2.urlopen(ftp_adress)) as r:
        with open(output_filename, 'wb') as f:
            shutil.copyfileobj(r, f)

    print('---- Data downloaded ----')
    return output_filename


def untar(filename):
    print('---- Uncompressing file %s ----' % (filename))
    output_dir = os.path.abspath(os.path.join(filename, os.pardir))

    tar = tarfile.open(filename, 'r:gz')
    tar.extractall(path=output_dir)
    tar.close()
    os.remove(filename)

    print('---- Uncompressed ----')


if __name__ == '__main__':
    main()

