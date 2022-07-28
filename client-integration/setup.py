import setuptools
import codecs

NAME = "irods-drs-client-support"
VERSION = "0.0.2"
AUTHOR = "Mike Conway"
EMAIL = "mike.conway@nih.gov"

with open("README.md", "r") as fh:
    long_description = fh.read()

install_requires = [
    "click>=7.0",
    "crc32c>=1.7",
    "PyYAML>=5.1.2",
    "requests>=2.22.0",
    "tqdm>=4.35.0",
    "ga4gh-drs-client>=0.1.7"
]

setuptools.setup(
    name=NAME,
    version=VERSION,
    author=AUTHOR,
    author_email=EMAIL,
    description="Supports iRODS DRS Client with tests and additional tools",
    long_description=long_description,
    long_description_content_type="text/markdown",
    url="https://github.com/NIEHS/irods-data-repository-service",
    package_data={'': ['*.yml']},
    packages=setuptools.find_packages(),
    install_requires=install_requires,
    classifiers=(
        "Programming Language :: Python :: 3.6",
        "Programming Language :: Python :: 3.7",
        "License :: OSI Approved :: BSD License",
        "Operating System :: OS Independent",
        "Natural Language :: English",
        "Topic :: Scientific/Engineering :: Bio-Informatics"
    ),
)
