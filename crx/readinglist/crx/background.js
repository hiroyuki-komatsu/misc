function openPage(tab) {
  chrome.tabs.create({"url": "readinglist.html"});
}

chrome.action.onClicked.addListener(openPage);

chrome.runtime.onInstalled.addListener(() => {
  chrome.contextMenus.create({
    "id": "weekly_summary",
    "title": "Weekly summary",
    "contexts": ["action"],
  });
});

chrome.contextMenus.onClicked.addListener((info, tab) => {
  if (info.menuItemId === "weekly_summary") {
    chrome.tabs.create({"url": "readinglist.html"});
  }
});
