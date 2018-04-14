import java.net.*;
import java.io.*;
import java.util.*;

public class Server
{

    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       = null;
    private DataOutputStream out=null;
    private int id;
	private String name;

    Thread t_in=null,t_out=null;

    static ArrayList<String> msg;
    static ArrayList<Boolean> msg_status;
	static ArrayList<String> user;
	static ArrayList<String> password;
	ArrayList<String> sender;

    public Server(int port)
    {
       try
       {
            server = new ServerSocket(port);
            System.out.println("Server started");
			msg=new ArrayList<String>();
			user=new ArrayList<String>();
			password=new ArrayList<String>();
			msg_status=new ArrayList<Boolean>();
	}
	catch(Exception e)
	{
		}
    }

    public Server(ServerSocket ss,Socket s)
    {
    	try
    	{
    		server=ss;
    		socket=s;
    		in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    		out=new DataOutputStream(socket.getOutputStream());
    	}
    	catch(Exception e)
    	{
    		}
		try
		{
			String a,b;
			a=in.readUTF();
			b=in.readUTF();
			sender=new ArrayList<String>();

			int x;
			x=user.indexOf(a);
			if(x==-1)
			{
				user.add(a);
				password.add(b);
				id=user.indexOf(a);
				msg_status.add(false);
				msg.add("");
			}
			else
			{
				if( ! b.equals( password.get(x) ) )
				{
					out.writeUTF("wrong");
					return;
					//Thread.stop();
				}
				id=x;
			}
			out.writeUTF("OK");
			name=a;
			System.out.println(name+" has succesfully joined");
		}
		catch(Exception e)
		{
        }



    	t_in=new Thread( new Runnable()
    				{
    					public void run()
    					{
    					    String line;
					    	while(true)
    						{
				    			try
				    			{
				    				line=in.readUTF();
				    				if( line.equalsIgnoreCase("exit") )
				    				{
					    				System.out.println(name+" has left conversation!");
					    				socket.close();
										t_out.stop();
										t_in.stop();
					    			}
									int i,x;
									String c_name,c_msg;
									x=line.indexOf('-');
									c_name=line.substring(0,x);
									c_msg=line.substring(x+1);
									i=0;
									sender.clear();
									String s="";
									while( i < c_name.length() )
									{
										if( c_name.charAt(i)!=',' )
											s=s+c_name.charAt(i);
										else
										{
											sender.add(s);
											s="";
										}
										i++;
									}
									sender.add(s);
									String name=in.readUTF();
									int y;
									for(i=0;i<sender.size();i++)
									{
										y=user.indexOf( sender.get(i) );
										if( y==-1 )
										{
											out.writeUTF(sender.get(i)+" doesn't exist");
										}
										else
										{
											msg.set(y,name+":: "+c_msg);
											msg_status.set(y,true);
										}
									}
					    		}
					  		catch(IOException e)
					    		{
					    			break;
					    			}
					    	}
				    	}
				  }
			);

	t_out=new Thread( new Runnable()
				{
    					public void run()
    					{
					    	while(true)
    						{

				    			try
				    			{

				    				Thread.sleep(100);

				    				if( msg_status.get(id)==false)
				    					continue;

				    				out.writeUTF( msg.get(id) );
				    				msg_status.set(id,false);
					    		}
					  		catch(Exception e)
					    		{
					    			break;
					    		}
					    	}
				    	}
				  }
			);
	t_in.start();
	t_out.start();


    }

	public static void main(String args[])throws Exception
	{
       	 Server sm = new Server(Integer.parseInt(args[0]));
       	 while(true)
       	 {
       	 	sm.socket=sm.server.accept();
       	 	new Server(sm.server,sm.socket);

       	 }
	}
}
