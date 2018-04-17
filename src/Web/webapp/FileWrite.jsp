<%@ page import = "java.io.*,java.util.*" %>

<html>
<body>
<h1> Lawns Processed! </h1>
<%
try
{
	//FileReader reader;
	//Scanner inFile;
	//File file = new File("test.txt");
	//String temp;
	//String val;
	//String paramName;

//	PrintWriter pw = new PrintWriter(new FileOutputStream("temp.txt"), true);

//	temp = request.getParameter("lawn1");
//	pw.println(temp);

//	temp = request.getParameter("comments1");
//	pw.println(temp);

//	temp = request.getParameter("lawn2");
//	pw.println(temp);

//	temp = request.getParameter("comments2");
//	pw.println(temp);

//	temp = request.getParameter("lawn3");
//	pw.println(temp);

//	temp = request.getParameter("comments3");
//	pw.println(temp);

//	pw.close();




	FileReader reader;
	Scanner inFile;
	File file = new File("lawns.txt");
	String temp;
	String val;
	String test;
	String bkpVal;
	String paramName;

	PrintWriter pw = new PrintWriter(new FileOutputStream("temp.txt"), true);

	reader = new FileReader(file);
	inFile = new Scanner(reader);




/*****START HERE*********************************************/

	Enumeration paramNames = request.getParameterNames();
	SortedSet ss = new TreeSet();



	while(paramNames.hasMoreElements())
	{
		
		temp = (String)paramNames.nextElement();
		int t = Integer.parseInt(temp);
		ss.add(t);
	}
	


	Iterator it = ss.iterator();
	while (it.hasNext()) 
	{
		//paramName = String.valueOf(it.next());
		//bkpVal = request.getParameter(paramName);
		
		paramName = String.valueOf(it.next());

		if(inFile.hasNext())
		{
			temp = inFile.nextLine();
			pw.println(temp);

			temp = inFile.nextLine();

			val = request.getParameter(paramName);
			
			if(val.equals("mowed"))
			{
				pw.println("mowed");
				temp = inFile.nextLine();

				paramName = String.valueOf(it.next());

				val = request.getParameter(paramName);
				
				if(val != null && !val.isEmpty())
				{
					pw.println(val);
				}
				else
				{
					pw.println("No Comment");
				}
			}
			
			else
			{
				pw.println("unmowed");
				temp = inFile.nextLine();
				
				paramName = String.valueOf(it.next());
				
				//val = "test";
				val = request.getParameter(paramName);

				if(val != null && !val.isEmpty())
				{
					pw.println(val);
					
				}

				else
				{
					pw.println("No Comment");
				}

			}
		}
	}
	
	pw.close();
	inFile.close();
	
	File file2 = new File("temp.txt");
	reader = new FileReader(file2);
	inFile = new Scanner(reader);
	pw = new PrintWriter(new FileOutputStream("lawns.txt"), true);
	
	while(inFile.hasNext())
	{
		temp = inFile.nextLine();
		pw.println(temp);
	}
	
	pw.close();
	inFile.close();
	file2.delete();

/*****END HERE*********************************************/





	//while(paramNames.hasMoreElements())
	//{
	//paramName = (String)paramNames.nextElement();
	//pw.println(paramName);
	//temp = request.getParameter(paramName);

	//pw.println(temp);
	//}

	//Map<String, String[]> parameters = request.getParameterMap();
	//for(String parameter : parameters.keySet())
	//{
		//vals = parameters.get(parameter);
		//pw.println(parameter);
		//pw.println(vals);
	//}

}catch(FileNotFoundException e){e.printStackTrace();}
%>
<h2> You Can Hit the Back Page in Your Browser to Return </h2>
</body>
</html>
