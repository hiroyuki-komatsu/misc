function addEntry(tab) {
  chrome.readingList.addEntry({
    title: tab.title,
    url: tab.url,
    hasBeenRead: false,
  });
}

function openSummary() {
  chrome.tabs.create({"url": "readinglist.html"});
}

chrome.action.onClicked.addListener(addEntry);

chrome.runtime.onInstalled.addListener(() => {
  chrome.contextMenus.create({
    "id": "add_entry",
    "title": "Add to Reading List",
    "contexts": ["page"],  // Right click on the page
  });
  chrome.contextMenus.create({
    "id": "weekly_summary",
    "title": "Weekly summary",
    "contexts": ["action"],  // Right click on the extension icon
  });
});

chrome.contextMenus.onClicked.addListener((info, tab) => {
  if (info.menuItemId === "add_entry") {
    addEntry(tab);
  }
  if (info.menuItemId === "weekly_summary") {
    openSummary();
  }
});
