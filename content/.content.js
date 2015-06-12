var x = document.createElement("INPUT");
x.setAttribute("type", "file");
x.setAttribute("id", "file");
x.setAttribute("onchange","myFunction()");
document.body.appendChild(x);



x = document.createElement("button");
x.setAttribute("id", "uploadFile");
x.setAttribute("value", "upload")
var t = document.createTextNode("Upload");
x.appendChild(t);
x.setAttribute("onClick", "uploadFile()");
document.body.appendChild(x);

x = document.createElement("P");
x.setAttribute("id", "file-info");
document.body.appendChild(x);

x = document.createElement("div");
x.setAttribute("id", "resp-div");
document.body.appendChild(x);

myFunction();


//http://www.w3schools.com/jsref/tryit.asp?filename=tryjsref_fileupload_files
function myFunction(){
    var x = document.getElementById("file");
    var txt = "";
    if ('files' in x) {
        if (x.files.length == 0) {
            txt = "Select a file.";
        } else {
                var file = x.files[0];
                if ('name' in file) {
                    txt += "name: <strong>" + file.name + "</strong><br>";
                }
                if ('size' in file) {
                    txt += "size: " + file.size + " bytes <br>";
                }            
        }
    } 
    else {
        if (x.value == "") {
            txt += "Select a file to upload.";
        } else {
            txt += "The file property is not supported by your browser!";
            txt  += "<br>The path of the selected file: " + x.value; // If the browser does not support the files property, it will return the path of the selected file instead. 
        }
    }
    document.getElementById("file-info").innerHTML = txt;
}

//https://developer.tizen.org/dev-guide/2.2.1/org.tizen.web.appprogramming/html/tutorials/w3c_tutorial/comm_tutorial/upload_ajax.htm
function uploadFile()
{
	var client = new XMLHttpRequest();
	var file = document.getElementById("file");
     
      /* Create a FormData instance */
    var formData = new FormData();
	//formData.append("filesize", file.files[0].size);
      /* Add the file */ 
    formData.append("upload", file.files[0]);
	client.open("post", "/upload", true);
    client.setRequestHeader("Content-Type", "multipart/form-data");
	client.setRequestHeader("File-Size",file.files[0].size)
    client.send(formData);  /* Send to server */ 
}