import java.net.*;
import java.io.*;

public class Client
{

    private Socket socket            = null;
    private DataInputStream  input   = null;
    private DataOutputStream out     = null;
    private DataInputStream  in      = null;
	String name,password;

    Thread t_in=null,t_out=null,t_timeOut=null;
	long time1,time2;

    public Client(String address, int port)
    {

        try
        {
            socket = new Socket(address, port);


            input  = new DataInputStream(System.in);

            out    = new DataOutputStream(socket.getOutputStream());
		in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }

		try
		{
			System.out.print("Enter name: ");
			name=input.readLine();
			System.out.println("Enter password: ");
			password=input.readLine();

			out.writeUTF(name);
			out.writeUTF(password);

			if( in.readUTF().equalsIgnoreCase("OK") )
			{
				System.out.println("Welcome "+name);
			}
			else
			{
				System.out.println("Incorrect password!! ");
				System.exit(0);
			}
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
								line=input.readLine();
								out.writeUTF(line);

								if(line.equalsIgnoreCase("exit"))
								{
									System.out.println("You have left "+name);
									socket.close();
						    		input.close();
						    		System.exit(0);
						    	}
								time1=System.currentTimeMillis();
								out.writeUTF(name);
						    }

						    	catch(Exception e)
						    	{
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
									System.out.println(in.readUTF());
									time1=System.currentTimeMillis();
						    }
						    	catch(Exception e)
						    	{
						    		}
						 }
					}
				}
			);
	t_timeOut= new Thread( new Runnable()
				{
					public void run()
					{
						while(true)
						{
							try
							{
								Thread.sleep(100);
								time2=System.currentTimeMillis();
								if(time2-time1>=70000)
								{
									out.writeUTF("bye");
									System.out.println("\n\nSession timed out!! ");
									System.exit(0);
								}
							}
							catch(Exception e)
							{
								}
						}
					}
				}
			);




	t_out.start();
	t_in.start();
	time1=System.currentTimeMillis();
	t_timeOut.start();
   }

    public static void main(String args[])
    {
        Client client = new Client("127.0.0.1", Integer.parseInt(args[0]));
    }
}
