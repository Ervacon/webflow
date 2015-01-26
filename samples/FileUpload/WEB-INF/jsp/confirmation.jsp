<%@ page session="false" %>

<%@ page import="com.ervacon.springframework.samples.fileupload.web.FileUploadBean"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
	<HEAD>
	</HEAD>
	<BODY>
		<FORM name="submitForm" action="upload">
			<INPUT type="hidden" name="_flowId" value="<%=request.getAttribute("flowId") %>">
			<INPUT type="hidden" name="_event" value="back">
		</FORM>
	
		<DIV align="left">File Contents</DIV>
		
		<HR>
		
		<DIV align="left">
			<%
				FileUploadBean file=(FileUploadBean)request.getAttribute("file"); 
				if (file.getFile()!=null && file.getFile().length>0) {
			%>
			<TABLE border="1">
				<TR>
					<TD><PRE><%=new String(file.getFile()) %></PRE></TD>
				</TR>
			</TABLE>
			<%
				}
				else {
			%>
				No file was uploaded!
			<%
				}
			%>
		</DIV>
		
		<HR>

		<DIV align="right">
			<INPUT type="button" onclick="javascript:document.submitForm.submit()" value="Back">
		</DIV>
		
	</BODY>
</HTML>
