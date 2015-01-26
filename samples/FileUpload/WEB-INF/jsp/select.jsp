<%@ page session="false" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
	<HEAD>
	</HEAD>
	<BODY>
	
		<DIV align="left">Select File</DIV>
		
		<HR>
		
		<DIV align="left">
			<FORM name="submitForm" action="upload" method="post" enctype="multipart/form-data">
				<INPUT type="hidden" name="_flowId" value="<%=request.getAttribute("flowId") %>">
				<INPUT type="hidden" name="_event" value="upload">
				
				<INPUT type="file" name="file">
			</FORM>
		</DIV>
		
		<HR>

		<DIV align="right">
			<INPUT type="button" onclick="javascript:document.submitForm.submit()" value="Upload">
		</DIV>
		
	</BODY>
</HTML>
