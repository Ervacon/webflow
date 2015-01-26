<%@ page session="false" %>

<%@ page import="java.util.*" %>
<%@ page import="com.ervacon.springframework.samples.phonebook.domain.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
	<HEAD>
	</HEAD>
	<BODY>
		<FORM name="newSearchForm" action="search">
			<INPUT type="hidden" name="_flowId" value="<%=request.getAttribute("flowId") %>">
			<INPUT type="hidden" name="_event" value="newSearch">
		</FORM>
	
		<DIV align="left">Search Results</DIV>
		
		<HR>
		
		<DIV align="left">
			<P>
				<TABLE border="1" width="100%">
					<TR>
						<TD><B>First Name</B></TD>
						<TD><B>Last Name</B></TD>
						<TD><B>User Id</B></TD>
						<TD><B>Phone</B></TD>
					</TR>
					<%
						List results=(List)request.getAttribute("persons");
						for (int i=0; i<results.size(); i++) {
							Person person=(Person)results.get(i);
					%>
						<TR>
							<TD><%=person.getFirstName() %></TD>
							<TD><%=person.getLastName() %></TD>
							<TD>
								<A href="search?_flowId=<%=request.getAttribute("flowId") %>&_event=detail&id=<%=person.getUserId() %>">
									<%=person.getUserId() %>
								</A>
							</TD>
							<TD><%=person.getPhone() %></TD>
						</TR>
					<%
						}
					%>
				</TABLE>
			</P>
		</DIV>
		
		<HR>

		<DIV align="right">
			<INPUT type="button" onclick="javascript:document.newSearchForm.submit()" value="New Search">
		</DIV>
		
	</BODY>
</HTML>
