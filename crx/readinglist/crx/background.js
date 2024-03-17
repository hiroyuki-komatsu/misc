function setIcon(isAdded) {
  const icon = isAdded ? "icon_added.png" : "icon.png";
  chrome.action.setIcon({path: {"128": icon}});
}

function addEntry(tab) {
  chrome.readingList.addEntry({
    title: tab.title,
    url: tab.url,
    hasBeenRead: false,
  });
}

function updateEntry(tab) {
  checkEntry(tab, (tab, isAdded) => {
    if (isAdded) {
      chrome.readingList.removeEntry({url: tab.url});
      setIcon(false);
    } else {
      addEntry(tab);
      setIcon(true);
    }
  });
}

// callback takes tab and boolean.
async function checkEntry(tab, callback) {
  if (!tab.url.startsWith("http")) {
    callback(tab, false);
    return;
  }
  chrome.readingList.query({ url: tab.url }, (entries) => {
    if (entries && entries.length > 0) {
      callback(tab, true);
    } else {
      callback(tab, false);
    }
  });
}

async function updateIcon(tab) {
  console.log("tab.url: " + tab.url);
  console.log("tab.title: " + tab.title);
  checkEntry(tab, (tab, isAdded) => {
    setIcon(isAdded);
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
