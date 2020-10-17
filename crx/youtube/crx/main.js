{
class Time {
  constructor(time) {
    time = Math.floor(time);
    const hour = Math.floor(time / 3600);
    time = time % 3600;
    const min = Math.floor(time / 60);
    const sec = time % 60;

    let param = "";
    let text = "";
    if(hour > 0) {
      param += hour + "h";
      text += hour + ":";
    }
    param += min + "m" + sec + "s";
    text += String(min).padStart(2, "0") + ":" + String(sec).padStart(2, "0");

    this.param = param;
    this.text = text;
  }
}

function getUrl(baseUrl, time) {
  const url = new URL(baseUrl);
  url.searchParams.set("t", time.param);
  return url.toString();
}

function getImageCanvas(player) {
  let canvas = document.createElement("canvas");
  canvas.width = player.videoWidth;
  canvas.height = player.videoHeight;
  canvas.getContext("2d").drawImage(player, 0, 0, canvas.width, canvas.height);
  return canvas;
}

function getImageUrl(canvas) {
  return canvas.toDataURL();
}

async function getImageBlob(canvas) {
  const blob = await new Promise(resolve => canvas.toBlob(resolve));
  return blob;
}

function createHtml(player) {
  const time = new Time(player.currentTime);
  const url = getUrl(player.baseURI, time);
  const prevTime = new Time(Math.max(player.currentTime - 10, 0));
  const prevUrl = getUrl(player.baseURI, prevTime);
  const canvas = getImageCanvas(player);
  const imageUrl = getImageUrl(canvas);
  const html = (
    `<img src='${imageUrl}'><br/>\n` +
    `<a href='${url}'>${time.text}</a> \n` +
    `(<a href='${prevUrl}'>« ${prevTime.text}</a>) \n`);
  return html;
}

function createText(player) {
  const time = new Time(player.currentTime);
  const url = getUrl(player.baseURI, time);
  const text = `[${time.text}](${url})`;
  return text;
}

function onCopyToClipVideo(event) {
  event.preventDefault();
  const player = document.getElementsByClassName("video-stream")[0];
  const html = createHtml(player);
  event.clipboardData.setData("text/html", html);
  const text = createText(player);
  event.clipboardData.setData("text/plain", text);
}

function main() {
  document.addEventListener("copy", onCopyToClipVideo);
  document.execCommand("copy");
  document.removeEventListener("copy", onCopyToClipVideo);
}

main();
}