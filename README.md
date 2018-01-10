# eudata
Tool collecting number of public contracts and their cost per country according to EU data

The tool consits of two main modules, Python script for downloading data, and Scala application used for analyzing it.

# Python script
data_downloader.py is a python script used to download data we wish to analyze.

Usage:
python data_downloader.py --start-date <YYYY-MM> --end-date <YYYY-MM> [--save-dir <filename>]
  
The script will download data from given start date to end date (inclusive) and uncompress them to target directory. If no directory is provided, files will be saved to ./data

# Scala application
Scala app is used to analyze downloaded data, it will output a csv file containing number of contracts per country and cost of awarded contracts in all currencies.

Usage:
scala EUDataCollector <data_directory> [<output_filename>]

Data is extracted according to ftp://ted.europa.eu/Resources/TED-XML_general_description_v1.2_20150422.pdf and some personal observations.

We assume that file with ID_DOCUMENT_TYPE 3 is notice of new contract and file with ID_DOCUMENT_TYPE 7 is notice of granting the contract. In our stats we will count number of new contracts in selected timeframe by counting files containing ID_DOCUMENT TYPE 3 and sum the value of granted contracts (their cost) according to files with ID_DOCUMENT_TYPE 7. We aknowledge all currencies that appear in selected timeframe. Most countries only deal in their own currency and euro (or only euro if it's the local currency), but there are a few outliers.
