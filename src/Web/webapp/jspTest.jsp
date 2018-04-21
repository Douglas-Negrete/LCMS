<html>
   <head>
      <title>Reading Checkbox Data</title>
   </head>
   
   <body>
      <h1>Reading Checkbox Data</h1>
      
      <ul>
         <li><p><b>Lawn 1:</b>
            <%= request.getParameter("lawn1")%>
         </p></li>
         <li><p><b>Comments 1:</b>
            <%= request.getParameter("comments1")%>
         </p></li>

	    <li><p><b>Lawn 2:</b>
            <%= request.getParameter("lawn2")%>
         </p></li>
         <li><p><b>Comments 2:</b>
            <%= request.getParameter("comments2")%>

		<li><p><b>Lawn 3:</b>
            <%= request.getParameter("lawn3")%>
         </p></li>
         <li><p><b>Comments 3:</b>
            <%= request.getParameter("comments3")%>

		<li><p><b>Lawn 4:</b>
            <%= request.getParameter("lawn4")%>
         </p></li>
         <li><p><b>Comments 4:</b>
            <%= request.getParameter("comments4")%>

		<li><p><b>Lawn 5:</b>
            <%= request.getParameter("lawn5")%>
         </p></li>
         <li><p><b>Comments 5:</b>
            <%= request.getParameter("comments5")%>
         </p></li>
      </ul>
   
   </body>
</html>