<%@ page import = "java.io.*,java.util.*" %>

<html>
<body>

<%
try
{
	FileReader reader;
	Scanner inFile;
	File file = new File("lawns.txt");
	String temp;
	String str;
	String paramName;

	reader = new FileReader(file);
	inFile = new Scanner(reader);

	PrintWriter pw = new PrintWriter(new FileOutputStream("temp.txt"), true)
	Enumeration paramNames = request.getParameterNames();
	while(paramNames.hasMoreElements())
	{

		paramName = (String)paramNames.nextElement();
		
		
		if(inFile.hasNext())
		{
			temp = inFile.nextLine();
			pw.println(temp);
			temp = inFile.nextLine();
			
			//str = request.getParameter(paramName);
			if(paramName != null)
			{
				pw.println("mowed");
				temp = inFile.nextLine();
				paramName = (String)paramNames.nextElement();
				pw.println(paramName);
			}

			else
			{
				pw.println("unmowed");
				temp = inFile.nextLine();
				paramName = (String)paramNames.nextElement();
				pw.println(paramName);
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
}catch(FileNotFoundException e){e.printStackTrace();}
%>

</html>
</body>