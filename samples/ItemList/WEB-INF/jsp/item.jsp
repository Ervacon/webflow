<%@ page session="false" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
	<HEAD>
	</HEAD>
	<BODY>
	
		<DIV align="left">Add Item</DIV>
		
		<HR>
		
		<DIV align="left">
			<FORM name="submitForm" action="il">
				<INPUT type="hidden" name="_flowId" value="<%=request.getAttribute("flowId") %>">
				<INPUT type="hidden" name="_currentState" value="item">
				<INPUT type="hidden" name="_event" value="add">
				
				<%-- make sure we send the token back to the server --%>
				<INPUT type="hidden" name="_token" value="<%=request.getAttribute("token") %>">
				
				<TABLE>
					<TR>
						<TD>Item Data</TD>
						<TD><INPUT type="text" name="data" value=""></TD>
					</TR>
				</TABLE>
			</FORM>
		</DIV>
		
		<HR>

		<DIV align="right">
			<INPUT type="button" onclick="javascript:document.submitForm.submit()" value="Add">
		</DIV>
		
	</BODY>
</HTML>
