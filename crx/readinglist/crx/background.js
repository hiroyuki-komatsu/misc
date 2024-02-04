function openPage(tab) {
  chrome.tabs.create({"url": "readinglist.html"});
}

chrome.action.onClicked.addListener(openPage);
