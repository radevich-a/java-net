import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;


    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            while (true){
                String command = in.readUTF();
                if ("upload".equals(command)){
                    try {
                        File file = new File("Server" + File.separator +in.readUTF());
                        if (!file.exists()){
                            file.createNewFile();
                        }
                        long size = in.readLong();
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[256];
                        for (int i = 0; i < (size + 256) / 256; i++) {
                            int read = in.read(buffer);
                            fos.write(buffer,0,read);
                        }
                        fos.close();
                        out.writeUTF("DONE");
                    } catch (Exception e){
                        out.writeUTF("ERROR");
                    }

                } else if ("download".equals(command)){
                    try {
                        File file = new File("Server" + File.separator +in.readUTF());

                        if (file.exists()){
                            long size = file.length();
                            out.writeLong(size);
                            FileInputStream fis = new FileInputStream(file);
                            int read = 0;
                            byte[] buffer = new byte[256];
                            while ((read = fis.read(buffer)) != -1){
                                out.write(buffer, 0, read);
                            }
                            out.flush();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                } else if ("remove".equals(command)){
                    try {
                        File file = new File("Server" + File.separator +in.readUTF());
                        if (file.exists()){
                            file.delete();
                                System.out.println(file.getName() + " " + "файл удален");
                        }else System.out.println("Файла" + file.getName() + " " +  "не обнаружено");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
