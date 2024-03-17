function addEntry(tab) {
  chrome.readingList.addEntry({
    title: tab.title,
    url: tab.url,
    hasBeenRead: false,
  });
}

function updateEntry(tab) {
  if (!tab.url.startsWith("http")) {
    return;
  }
  chrome.readingList.query({ url: tab.url }, (entries) => {
    if (entries && entries.length > 0) {
      chrome.readingList.removeEntry({url: tab.url});
      chrome.action.setIcon({path: {"128": "icon.png"}});
    } else {
      chrome.readingList.addEntry({
        title: tab.title,
        url: tab.url,
        hasBeenRead: false,
      });
      chrome.action.setIcon({path: {"128": "icon_added.png"}});
    }
  });
}

async function updateIcon(tab) {
  console.log("tab.url: " + tab.url);
  console.log("tab.title: " + tab.title);
  if (!tab.url.startsWith("http")) {
    chrome.action.setIcon({path: {"128": "icon.png"}});
    return;
  }
  chrome.readingList.query({ url: tab.url }, (entries) => {
    if (entries && entries.length > 0) {
      chrome.action.setIcon({path: {"128": "icon_added.png"}});
    } else {
      chrome.action.setIcon({path: {"128": "icon.png"}});
    }
  });
}

async function onTabActivated(info) {
  chrome.tabs.get(info.tabId, updateIcon);
}

async function onWindowFocusChanged(windowId) {
  chrome.tabs.query({"active": true, "windowId": windowId}, (tabs) => {
    if (tabs.length > 0) {
      updateIcon(tabs[0]);
    }
  });
}

function openSummary() {
  chrome.tabs.create({"url": "readinglist.html"});
}

chrome.action.onClicked.addListener(updateEntry);

chrome.tabs.onActivated.addListener(onTabActivated);
chrome.windows.onFocusChanged.addListener(onWindowFocusChanged);

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
