<%@ page import = "java.io.*,java.util.*" %>

<html>
<body>
<h1> Processing Lawns </h1>
<%
try
{
	FileReader reader;
	Scanner inFile;
	File file = new File("lawns.txt");
	String temp;
	String val;
	String paramName;

	reader = new FileReader(file);
	inFile = new Scanner(reader);

	PrintWriter pw = new PrintWriter(new FileOutputStream("temp.txt"), true);
	Enumeration paramNames = request.getParameterNames();
	
	while(paramNames.hasMoreElements())
	{

		paramName = (String)paramNames.nextElement();
		
		
		if(inFile.hasNext())
		{
		
			temp = inFile.nextLine();
			pw.println(temp);
			
			temp = inFile.nextLine();
			
			val = request.getParameter(paramName);
			
			if(val != null)
			{
				pw.println("mowed");
				temp = inFile.nextLine();
				
				paramName = (String)paramNames.nextElement();
				val = request.getParameter(paramName);
				pw.println(val);
			}

			else
			{
				pw.println("unmowed");
				temp = inFile.nextLine();
				
				paramName = (String)paramNames.nextElement();
				val = request.getParameter(paramName);
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

</body>
</html>
