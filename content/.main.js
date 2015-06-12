function dragElement(event) {
    event.dataTransfer.setData("text", event.target.id);
	console.log("Moving " + event.dataTransfer.getData("Text"));
}
function dropElementOnIframe(event) {
    event.preventDefault();
	var data = event.dataTransfer.getData("text");	    
	console.log("Inside dropElementOnIframe");
    //event.target.appendChild(document.getElementById(data));
}
function dropElement(event) {
    event.preventDefault();
	var data = event.dataTransfer.getData("text");	    
	var CopyToDiv = document.getElementById("DropHere");
	if(CopyToDiv.tagName.toLowerCase() === "img")
		console.log("Droping on an image");
	if(CopyToDiv.childElementCount === 1)
	{
		CopyToDiv.removeChild(CopyToDiv.lastChild);
	}
    CopyToDiv.appendChild(document.getElementById(data).cloneNode(true));
	//CopyToDiv.innerHTML = document.getElementById(data).id;
	document.getElementById("infoFrame").setAttribute("src","https://en.wikipedia.org/w/index.php?search=" + document.getElementById(data).alt +"&title=Special%3ASearch&go=Go");
	//document.getElementById("infoFrame").setAttribute("src","http://css-tricks.com/snippets/php/display-styled-directory-contents/");
	
}
function allowDrop(ev) {
    ev.preventDefault();
}



var images = [
				{src:"superman", 	alt:"Superman",					style:"width:256px;height:256px"},
				{src:"spiderman", 	alt:"Spiderman", 				style:"width:256px;height:256px"},
				{src:"l", 			alt:"L (Death Note)", 			style:"width:256px;height:256px"},
				{src:"cs", 			alt:"Counter Strike", 			style:"width:256px;height:256px"},
				{src:"gta", 		alt:"Grand Theft Auto Game", 	style:"width:256px;height:256px"}
			];

//var imageDiv = document.createElement("div");
//imageDiv.setAttribute("id","DragFromHere");
//Load all the images
for(i in images)
{
	var tmpImg = document.createElement("img");
	tmpImg.setAttribute("src",images[i].src);
	tmpImg.setAttribute("alt",images[i].alt);
	tmpImg.setAttribute("style",images[i].style);	
	tmpImg.setAttribute("id",images[i].alt);
	//Allow the image to be dragged(Optional)
	tmpImg.setAttribute("draggable","true");
	//assign ondragstart function
	tmpImg.setAttribute("ondragstart","dragElement(event)");		
	//imageDiv.appendChild(tmpImg);	
	document.body.appendChild(tmpImg);	
	
}
//document.body.appendChild(imageDiv);
var dropDiv = document.createElement("div");
dropDiv.setAttribute("style","width:256px;height:256px;border:1px solid;overflow:hidden");
dropDiv.setAttribute("id","DropHere");
dropDiv.setAttribute("ondrop","dropElement(event)");
dropDiv.setAttribute("ondragover","allowDrop(event)");
dropDiv.setAttribute("effectAllowed","all");
dropDiv.setAttribute("dropEffect","copy");
dropDiv.setAttribute("float","left");
//dropDiv.setAttribute("class","droptarget");
document.body.appendChild(dropDiv);

var wikiFrame = document.createElement("iframe");
wikiFrame.setAttribute("id","infoFrame");
wikiFrame.setAttribute("style","width:100%;height:1000px");
wikiFrame.setAttribute("frameborder","0");
wikiFrame.setAttribute("ondrop","dropElementOnIframe(event)");
wikiFrame.setAttribute("ondragover","allowDrop(event)");
document.body.appendChild(wikiFrame);


/*var copyDiv = document.createElement("div");
copyDiv.setAttribute("id","CopyHere");
copyDiv.setAttribute("style","width:256px;height:256px;border:1px solid;");
document.body.appendChild(copyDiv);*/

function iFrameOnload() {
console.log("Enter OnLoad");
        iFrame = document.getElementById('infoFrame');
        iFrame.height = "";
        iFrame.height = iFrame.contentWindow.document.body.scrollHeight + "px";
console.log("Exit OnLoad");
   }   
   