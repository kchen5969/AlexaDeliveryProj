__author__ = 'llilian'

# from scrapper import BeautifulSoup

# from scrapper import bea
from bs4 import BeautifulSoup

import os, sys


def parse_search_result(html):
    print("Check!")

    soup = BeautifulSoup(html, 'html.parser')

    results = soup.findAll("li", "s-result-item")

    print "{} results from the search page.\n".format(len(results))
    # print "Sample Result:\n{}\n".format(results[0])

    response = []

    for result in results:
        if result is None:
            continue
        else:
            asin = result['data-asin']
            # print("\nASIN {}".format(asin))
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
                    if fast_track_div is None:
                        fast_track_msg = "No Fast Track"
                    else:
                        fast_track_msg_span = fast_track_div.find("span", class_="a-size-small a-color-secondary")
                        if fast_track_msg_span is None:
                            fast_track_msg = "No Fast Track"
                        else:
                            msg = fast_track_msg_span.text
                            if "Get it by" in msg:
                                fast_track_msg = msg
                            else:
                                fast_track_msg = "No Fast Track"
                            # print("\nFAST TRACK {}".format(fast_track_msg))
                        title = title_a['title']
                        # print("\nTITLE {}".format(str(title.encode('utf8'))))
                        response.append("{}#{}#{}".format(asin, str(title.encode('utf8')), fast_track_msg))
    return response


def main():

    dirname, filename = os.path.split(os.path.abspath(sys.argv[0]))

    html_file_bulb= os.path.join(dirname, "lightbulb.html")
    html_file_alienware = os.path.join(dirname, "alienware.html")
    html_file_game_console = os.path.join(dirname, "nintendo.html")
    html_file_lenovo = os.path.join(dirname, "lenovo.html")
    html_file_headphone = os.path.join(dirname, "headphone.html")
    # current scrapper does not work with sneakers, food, etc. search
    html_file_sneakers = os.path.join(dirname, "sneakers.html")

    test_list = [html_file_bulb, html_file_alienware, html_file_game_console, html_file_lenovo, html_file_headphone]
    # test_list = [html_file_headphone]
    # test_list = [html_file_lenovo]

    for test_html_file in test_list:
        with open(test_html_file, 'r') as my_file:
            data = my_file.read()
        parsed_results = parse_search_result(data)
        print("Titles size :\n{}\n".format(len(parsed_results)))
        print("\n".join(parsed_results))

if __name__ == "__main__":
    main()
