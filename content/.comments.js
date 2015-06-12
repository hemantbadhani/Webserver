/**
 * This script does the following:
 * 1. Loads current comments form .comments.txt
 * 2. Sends new comments to host through a POST message
 * 3. Shows latest comment on top 
 * 
 */
var commentBox = document.createElement("textarea");
commentBox.setAttributes("id","CommentBox");
commentBox.setAttributes("rows","10");
commentBox.setAttributes("cols","20");
document.body.appendChild(commentBox);
<form>
<textarea id="words" rows="10" cols="20">Enter comment</textarea>
<input type="button" onclick="getwords()" value="Enter" /> <br>
<p id="para"></p>
</form>