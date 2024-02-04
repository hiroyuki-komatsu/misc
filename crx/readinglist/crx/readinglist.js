function getDateString(date) {
  const tz_offset_msec = date.getTimezoneOffset() * 60 * 1000;
  // Since toISOString always uses UTC, adjusts the timezone offset to shift
  // the local date to the UTC.
  const adjusted_date = new Date(date.getTime() - tz_offset_msec);
  return adjusted_date.toISOString().slice(0, 10);
}

function getWeekRange(date) {
  const week = (date.getDay() + 6) % 7;  // Shifts to the Monday origin.
  const today = new Date(date.getFullYear(), date.getMonth(), date.getDate());
  const a_day_msec = 86400 * 1000;
  const monday = new Date(today.getTime() - a_day_msec * week);
  const sunday = new Date(today.getTime() + a_day_msec * (6 - week));
  return [getDateString(monday), getDateString(sunday)];
}

function createHtml(items) {
  // Sorts items in descending order.
  items.sort((a, b) => { return b.lastUpdateTime - a.lastUpdateTime; });

  let text = "";
  let prev_range = "";
  for (const item of items) {
    const date = new Date(item.lastUpdateTime);
    const week_range = getWeekRange(date).join(" - ");
    if (week_range != prev_range) {
      text += `<h2>${week_range}</h2>\n`;
      prev_range = week_range;
    }
    text += `<a href="${item.url}">${item.title}</a><br/>\n`;
  }
  return text;
}

window.addEventListener("load", async (event) => {
  const items = await chrome.readingList.query({});
  document.getElementById("list").innerHTML = createHtml(items);
});
