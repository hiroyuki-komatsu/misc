// Keep track of the current active tab.
var currentTab = null;

// Record the current active tab.
function recordTab() {
  currentTab = chrome.browserAction.getFocusedTab();
}

// Activate the recorded tab.
function activateTab() {
  chrome.browserAction.setActive(currentTab);
}

// Listen for keyboard shortcuts.
chrome.browserAction.addListener(function() {
  // If the user presses Shift-Cmd-S, record the current active tab.
  if (event.keyCode === 69 && event.shiftKey && event.commandKey) {
    recordTab();
  }

  // If the user presses Shift-Cmd-O, activate the recorded tab.
  if (event.keyCode === 79 && event.shiftKey && event.commandKey) {
    activateTab();
  }
});

