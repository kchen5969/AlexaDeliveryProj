__author__ = 'llilian'

# from scrapper import BeautifulSoup

# from scrapper import bea
from bs4 import BeautifulSoup

import os, sys


def my_handler(html):
    print("Check!")

    soup = BeautifulSoup(html, 'html.parser')

    results = soup.findAll("li", "s-result-item")

    print "{} results from the search page.\n".format(len(results))
    # print "Sample Result:\n{}\n".format(results[0])

    response = []

    # todo - get rid of sponsored results

    for result in results:
        if result is None:
            continue
        else:
            sponsor = result.find("h5", class_="s-sponsored-list-header")
            if sponsor is not None:
                # this is a sponsored result, skip
                continue
            else:
                title_a = result.find("a", class_="s-access-detail-page")
                fast_track_div = result.find("div", class_="a-row a-spacing-mini")
                if title_a is None:
                    continue
                else:
                    title = title_a['title']
                    fast_track_msg_span = fast_track_div.find("span", class_="a-size-small a-color-secondary")
                    if fast_track_msg_span is None:
                        fast_track_msg = "No Fast Track"
                    else:
                        msg = fast_track_msg_span.text
                        if "Get it by" in msg:
                            fast_track_msg = msg
                        else:
                            fast_track_msg = "No Fast Track"
                    response.append("{}#{}".format(title, fast_track_msg))
    print("Titles size :\n{}\n".format(len(response)))
    print("\n".join(response))
    return response


def main():

    dirname, filename = os.path.split(os.path.abspath(sys.argv[0]))

    html_file = os.path.join(dirname, "alienware.html")

    with open(html_file, 'r') as my_file:
        data = my_file.read()

    # print(data)

    my_handler(data)

if __name__ == "__main__":
    main()
