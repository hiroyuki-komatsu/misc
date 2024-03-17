const toggleEntryId = "toggle_entry";
const weeklySummaryId = "weekly_summary";

function getIconPath(isAdded) {
  return isAdded ? "icon_added.png" : "icon.png";
}

function getToggleEntryTitle(isAdded) {
  return isAdded ? "Remove from Reading List" : "Add to Reading List";
}

function setStatus(isAdded) {
  chrome.action.setIcon({path: {"128": getIconPath(isAdded)}});

  chrome.contextMenus.update(toggleEntryId, {
    "title": getToggleEntryTitle(isAdded),
    "contexts": ["page"],  // Right click on the page
  });
}

function addEntry(tab) {
  chrome.readingList.addEntry({
    title: tab.title,
    url: tab.url,
    hasBeenRead: false,
  });
}

function toggleEntry(tab) {
  checkEntry(tab, (isAdded) => {
    if (isAdded) {
      chrome.readingList.removeEntry({url: tab.url});
      setStatus(false);
    } else {
      addEntry(tab);
      setStatus(true);
    }
  });
}

// callback takes boolean.
async function checkEntry(tab, callback) {
  if (!tab.url.startsWith("http")) {
    callback(false);
    return;
  }
  chrome.readingList.query({ url: tab.url }, (entries) => {
    const hasEntry = (entries && entries.length > 0);
    callback(hasEntry);
  });
}

async function updateStatus(tab) {
  checkEntry(tab, setStatus);
}

async function onTabActivated(info) {
  chrome.tabs.get(info.tabId, updateStatus);
}

async function onWindowFocusChanged(windowId) {
  chrome.tabs.query({"active": true, "windowId": windowId}, (tabs) => {
    if (tabs.length > 0) {
      updateStatus(tabs[0]);
    }
  });
}

function openSummary() {
  chrome.tabs.create({"url": "readinglist.html"});
}

// Listeners

chrome.action.onClicked.addListener(toggleEntry);

chrome.tabs.onActivated.addListener(onTabActivated);
chrome.windows.onFocusChanged.addListener(onWindowFocusChanged);

// Context menu

chrome.runtime.onInstalled.addListener(() => {
  chrome.contextMenus.create({
    "id": toggleEntryId,
    "title": getToggleEntryTitle(/*isAdded=*/false),
    "contexts": ["page"],  // Right click on the page
  });
  chrome.contextMenus.create({
    "id": weeklySummaryId,
    "title": "Weekly summary",
    "contexts": ["action"],  // Right click on the extension icon
  });
});

chrome.contextMenus.onClicked.addListener((info, tab) => {
  if (info.menuItemId === toggleEntryId) {
    toggleEntry(tab);
  }
  if (info.menuItemId === weeklySummaryId) {
    openSummary();
  }
});
