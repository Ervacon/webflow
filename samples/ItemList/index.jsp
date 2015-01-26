<%@ page session="true" %> <%-- make sure we have a session --%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
	<HEAD>
	</HEAD>
	<BODY>
	
		<DIV align="left">Item List - A Spring Web Flow Sample</DIV>
		
		<HR>
		
		<DIV align="left">
			<P>
				<A href="itemList/il">Item List</A>
			</P>
			
			<P>
				This Spring web flow sample application illustrates 2 things:
				<UL>
					<LI>
						Double submit prevention using the synchronizer token
						capabilities offered by the WebFlowUtils class: refreshing
						or back button use will not allow you to resubmit data.
					</LI>
					<LI>
						Flow expiry and cleanup using the WebFlowCleanupFilter:
						after 1 minute of idle time, a web flow will expire and
						will no longer be available for request processing.
					</LI>
				</UL>
			</P>
		</DIV>
		
		<HR>

		<DIV align="right"></DIV>
		
	</BODY>
</HTML>
