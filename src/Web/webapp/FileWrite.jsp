<%@ page import = "java.io.*,java.util.*" %>

<html>
<body>
<h1> Lawns Processed! </h1>
<%
try
{

	FileReader reader;
	Scanner inFile;
	File file = new File("lawns.txt");
	String temp;
	String val;
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
	
	temp = inFile.nextLine();
    pw.println(temp);
	
	while (it.hasNext()) 
	{
		
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
					if(temp.equals("No Comment"))
					{
						pw.println(val);
					}
					
					else
					{
						pw.println(temp + " " + val);
					}
				}
				else
				{
                    if(temp != null && !temp.isEmpty())
                    {
                        pw.println(temp);
                    }
                    else
                    {
                        pw.println("No Comment");
                    }
				}
			}
			
			else
			{
				pw.println("unmowed");
				temp = inFile.nextLine();
				
				paramName = String.valueOf(it.next());
				
				val = request.getParameter(paramName);

				if(val != null && !val.isEmpty())
				{
					if(temp.equals("No Comment"))
					{
						pw.println(val);
					}
					
					else
					{
						pw.println(temp + " " + val);
					}
					
				}

				else
				{
                    if(temp != null && !temp.isEmpty())
                    {
                        pw.println(temp);
                    }
                    else
                    {
                        pw.println("No Comment");
                    }
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

}catch(FileNotFoundException e){e.printStackTrace();}
%>
<h2> Comment changes won't be reflected on the Web Page </h2>
<h2> You Can Hit Back in Your Browser to Return </h2>
</body>
</html>
