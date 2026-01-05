import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import RMI.ByteService;
import RMI.CharacterService;
import RMI.DataService;
import UDP.Book;
import UDP.Student;

import java.util.*;
import java.net.*;
import java.io.*;

public class mainCustomer {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("203.162.10.109", 2209);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        String code = "B22DCDT095;K37Zp3Fm";
        out.writeObject(code);
        out.flush();

        Customer customer = (Customer) in.readObject();

        chuan(customer);

        out.writeObject(customer);
        out.flush();

        out.close();
        in.close();
        socket.close();
    }

    public static void chuan(Customer c) {
        String str = c.getName().trim();
        String[] tmpten = str.split("\\s+");

        String ten = tmpten[tmpten.length - 1].toUpperCase();
        String ho = "";
        for (int i = 0; i < tmpten.length - 1; i++) {
            String p = tmpten[i].toLowerCase();
            ho += Character.toUpperCase(p.charAt(0)) + p.substring(1) + " ";
        }
        ten += ", " + ho;
        c.setName(ten);

        String dob = c.getDayOfBirth();
        String[] d = dob.split("-");
        String newDob = d[1] + "/" + d[0] + "/" + d[2];
        c.setDayOfBirth(newDob);
        
        String u = "";
        for (int i = 0; i < tmpten.length - 1; i++) u += Character.toLowerCase(tmpten[i].charAt(0));
        u += tmpten[tmpten.length - 1].toLowerCase();
        c.setUserName(u);
    }
}






Employee UDP

public class mainEmployee {
    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCDT095";
        String qCode = "HmBpaWNU";

        InetAddress serverIP = InetAddress.getByName("203.162.10.109");
        int serverPort = 2209;

        DatagramSocket socket = new DatagramSocket();

        String msg = ";" + studentCode + ";" + qCode;
        byte[] sendData = msg.getBytes();

        socket.send(new DatagramPacket(sendData, sendData.length, serverIP, serverPort));
        byte[] receiveBuffer = new byte[4096];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);

        byte[] data = receivePacket.getData();

        byte[] requestIdBytes = new byte[8];
        System.arraycopy(data, 0, requestIdBytes, 0, 8);
        String requestId = new String(requestIdBytes).trim();

        ByteArrayInputStream bis =
                new ByteArrayInputStream(data, 8, receivePacket.getLength() - 8);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Employee emp = (Employee) ois.readObject();

        String[] nameParts = emp.getName().toLowerCase().split("\\s+");
        StringBuilder newName = new StringBuilder();
        for (String p : nameParts) {
            newName.append(Character.toUpperCase(p.charAt(0)))
                   .append(p.substring(1))
                   .append(" ");
        }
        emp.setName(newName.toString().trim());

        String year = emp.getHireDate().split("-")[0];
        int x = 0;
        for (char c : year.toCharArray()) {
            x += c - '0';
        }
        emp.setSalary(emp.getSalary() * (1 + x / 100.0));

        String[] d = emp.getHireDate().split("-");
        emp.setHireDate(d[2] + "/" + d[1] + "/" + d[0]);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(emp);
        oos.flush();

        byte[] empBytes = bos.toByteArray();
        byte[] sendBack = new byte[8 + empBytes.length];

        System.arraycopy(requestIdBytes, 0, sendBack, 0, 8);
        System.arraycopy(empBytes, 0, sendBack, 8, empBytes.length);

        socket.send(new DatagramPacket(sendBack, sendBack.length, serverIP, serverPort));

        socket.close();
    }
}





Laptop TCP

package TCP;

import java.io.*;
import java.net.Socket;

public class mainLaptop {
    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT108";
        String qCode = "55dQR6pr";

        Socket socket = new Socket("203.162.10.109", 2209);

        ObjectOutputStream out =
                new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in =
                new ObjectInputStream(socket.getInputStream());
        out.writeObject(studentCode + ";" + qCode);
        out.flush();
        Laptop laptop = (Laptop) in.readObject();

        String[] words = laptop.getName().split("\\s+");
        if (words.length >= 2) {
            String temp = words[0];
            words[0] = words[words.length - 1];
            words[words.length - 1] = temp;
        }
        String newName = String.join(" ", words);
        laptop.setName(newName);

        int qty = laptop.getQuantity();
        int reversed = Integer.parseInt(
                new StringBuilder(String.valueOf(qty)).reverse().toString()
        );
        laptop.setQuantity(reversed);

        out.writeObject(laptop);
        out.flush();

        in.close();
        out.close();
        socket.close();
    }
}




Book UDP

public class mainBook {
    private static final int MAX_RETRIES = 3;
    private static final int TIMEOUT = 5000;
    private static final int BUFFER_SIZE = 8192;

    public static void main(String[] args) {
        DatagramSocket socket = null;
        
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT);

            InetAddress serverAddr = InetAddress.getByName("203.162.10.109");
            int port = 2209;

            String request = ";B23DCCN005;NOoOCuN4";
            byte[] sendData = request.getBytes("UTF-8");
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr, port);
            socket.send(sendPacket);
            System.out.println("Đã gửi yêu cầu đến server...");

            byte[] receiveData = new byte[8192];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            
            boolean received = false;
            for (int retry = 0; retry < MAX_RETRIES && !received; retry++) {
                try {
                    socket.receive(receivePacket);
                    received = true;
                    System.out.println("Nhận phản hồi từ server...");
                } catch (SocketTimeoutException e) {
                    if (retry < MAX_RETRIES - 1) {
                        System.out.println("Timeout, thử lại lần " + (retry + 1) + "...");
                        socket.send(sendPacket);
                    } else {
                        throw e;
                    }
                }
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(receivePacket.getData(), 0, receivePacket.getLength());
            byte[] reqIdBytes = new byte[8];
            bis.read(reqIdBytes);
            String requestId = new String(reqIdBytes, "UTF-8").trim();
            System.out.println("RequestID: " + requestId);

            Book book;
            try (ObjectInputStream ois = new ObjectInputStream(bis)) {
                book = (Book) ois.readObject();
                System.out.println("Book nhận được: " + book);
            }

            book.setTitle(capitalizeTitle(book.getTitle()));
            book.setAuthor(normalizeAuthor(book.getAuthor()));
            book.setIsbn(formatIsbn(book.getIsbn()));
            book.setPublishDate(convertDate(book.getPublishDate()));

            System.out.println("Book sau chuẩn hóa: " + book);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(reqIdBytes);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(book);
            oos.flush();

            byte[] sendBack = bos.toByteArray();
            DatagramPacket sendBackPacket = new DatagramPacket(sendBack, sendBack.length, serverAddr, port);
            socket.send(sendBackPacket);
            System.out.println("Đã gửi Book chuẩn hóa về server!");

        } catch (SocketTimeoutException e) {
            System.err.println("Lỗi: Không nhận được phản hồi từ server sau " + (MAX_RETRIES) + " lần thử.");
            if (socket != null && !socket.isClosed()) socket.close();
            return;
        } catch (IOException e) {
            System.err.println("Lỗi I/O: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi khi đọc đối tượng Book: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) socket.close();
        }
    }

    private static String capitalizeTitle(String title) {
        if (title == null) return "";
        String[] words = title.toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    private static String normalizeAuthor(String author) {
        if (author == null) return "";
        String[] words = capitalizeTitle(author).split("\\s+");
        if (words.length == 0) return "";
        String last = words[words.length - 1].toUpperCase();
        StringBuilder rest = new StringBuilder();
        for (int i = 0; i < words.length - 1; i++) {
            rest.append(words[i]).append(" ");
        }
        return last + ", " + rest.toString().trim();
    }

    private static String convertDate(String date) {
        if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) return date;
        String[] parts = date.split("-");
        return parts[1] + "/" + parts[0];
    }

    private static String formatIsbn(String isbn) {
        if (isbn == null || isbn.isEmpty()) return isbn;

        isbn = isbn.replaceAll("[-\\s]", "");

        if (isbn.length() != 13) return isbn;
        return String.format("%s-%s-%s-%s-%s",
            isbn.substring(0, 3),
            isbn.substring(3, 4),
            isbn.substring(4, 6),
            isbn.substring(6, 12),
            isbn.substring(12));
    }
}










Student UDP

public class sinhvien {
	public static String chuanhoa(String s) {
        s = s.trim();
        s = s.toLowerCase();
        String a[] = s.split("\\s+");
        String res = "";
        for (String x : a) {
            res += Character.toUpperCase(x.charAt(0));
            res += x.substring(1);
            res += " ";
        }
        res = res.substring(0, res.length() - 1);
        return res;
    }
	
	
	public static String chEmail(String s) {
	    s = s.trim();
	    s = s.toLowerCase();
	    String a[] = s.split("\\s+");
	    String res = "";

	    res += a[a.length - 1];
	    for (int i = 0; i < a.length - 1; i++) {
	        res += a[i].charAt(0);
	    }

	    res += "@ptit.edu.vn";
	    return res;
	}
	
	
	public static void main(String[] args) throws Exception{
		DatagramSocket socket = new DatagramSocket();
		InetAddress sA = InetAddress.getByName("203.162.10.109");
		int sP = 2209;
		
		String code = ";B22DCDT095;8irwRvvB";
		DatagramPacket dpGui = new DatagramPacket(code.getBytes(), code.length(), sA, sP);
		socket.send(dpGui);
		
		byte[] buffer = new byte[1024];
		DatagramPacket dpNhan = new DatagramPacket(buffer, buffer.length);
		socket.receive(dpNhan);
		String tmp = new String(dpNhan.getData(),0,8);  
		System.out.println(tmp);
		
		ByteArrayInputStream input = new ByteArrayInputStream(dpNhan.getData(), 8, dpNhan.getLength()-8);
		ObjectInputStream in = new ObjectInputStream(input);
		Student sv = (Student) in.readObject();
		System.out.println(sv);
		
		
		// Xu lý code ơ day
		sv.name = chuanhoa(sv.name);
		sv.email = chEmail(sv.name);
		
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(output);
		out.writeObject(sv);
		out.flush();
		byte gui2[] = new byte[8 + output.size()];
		System.arraycopy(tmp.getBytes(), 0, gui2, 0, 8);
		System.arraycopy(output.toByteArray(), 0, gui2, 8, output.size());
		DatagramPacket dpGui1 = new DatagramPacket(gui2, gui2.length, sA, sP);
		socket.send(dpGui1);
		
		
		socket.close();
	}
}





Loai bo ki tu dac biet va trung nhau


public class Baitap1{
	public static void main(String[] args) throws Exception{
		DatagramSocket socket = new DatagramSocket();
		InetAddress sA = InetAddress.getByName("203.162.10.109");
		int sP = 2208;
		
		String ma = ";B22dcdt095;ZGX0dO1F";
		DatagramPacket dpGui = new DatagramPacket(ma.getBytes(), ma.length(), sA, sP);
		socket.send(dpGui);
		
		byte buffer[] = new byte[1024];
		DatagramPacket dpNhan = new DatagramPacket(buffer, buffer.length);
		socket.receive(dpNhan);
		String s = new String(dpNhan.getData(), 0, dpNhan.getLength());
		String[] tmp = s.trim().split(";");
		String rqId = tmp[0];
		String s1 = tmp[1];
		String s2 = tmp[2];
		
		boolean[] dd = new boolean[256];
		for(char c : s2.toCharArray()) {
			dd[c] = true;
		}
		String res = rqId + ";";
		for(char c: s1.toCharArray()) {
			if(!dd[c]) res += c;
		}
		DatagramPacket dpGui1 = new DatagramPacket(res.getBytes(), res.length(), sA, sP);
		socket.send(dpGui1);
		
		socket.close();
	}
}






Gia tri lon nhat va gia tri nho nhat

public class Baitap1{
	public static void main(String[] args) throws Exception{
		DatagramSocket socket = new DatagramSocket();
		InetAddress sA = InetAddress.getByName("203.162.10.109");
		int sP = 2207;
		
		String ma = ";b22dcdt095;jljEVajR";
		DatagramPacket dpGui = new DatagramPacket(ma.getBytes(), ma.length(), sA, sP);
		socket.send(dpGui);
		
		byte buffer[] = new byte[1024];
		DatagramPacket dpNhan = new DatagramPacket(buffer, buffer.length);
		socket.receive(dpNhan);
		String s = new String(dpNhan.getData(), 0, dpNhan.getLength());
		s = s.trim();
		s = s.replace(',', ' ');
		s = s.replace(';', ' ');
		String[] tmp = s.split("\\s+");
		String res = tmp[0] + ";";
		
		ArrayList<Integer> a = new ArrayList<>();
		for(int i=1;i<tmp.length;i++) {
			a.add(Integer.parseInt(tmp[i]));
		}
		a.sort(null);
		res += String.format("%d,", a.get(a.size()-1));
		res += String.format("%d", a.get(0));
		
		DatagramPacket dpGui1 = new DatagramPacket(res.getBytes(), res.length(), sA, sP);
		socket.send(dpGui1);
		socket.close();
	}
}




Menh gia dong xu

public class DS {
    public static void main(String[] args) throws Exception {
        Registry rg = LocateRegistry.getRegistry("203.162.10.109", 1099);
        DataService sv = (DataService) rg.lookup("RMIDataService");
        String ma = "b22dcat108", qCode = "4WgddEn6";
        int n = (int) sv.requestData(ma, qCode), res = 0;

        String ans = "";
        int[] a = {1, 2, 5, 10}; 
        for (int i = 3; i >= 0; i--) {
            int p = n / a[i]; 
            if (p > 0) {
                res += p; 
                n -= p * a[i]; 
                for (int j = 0; j < p; j++) ans+=a[i] + ",";
            }
        }
        if (n > 0) ans = "-1";
        else {
            ans = ans.substring(0, ans.length() - 1);
            ans= String.format("%d; ", res) + ans;
        }
        sv.submitData(ma, qCode, ans);
    }
}








To hop ke tiep

public class NextPermutationClient {
    static boolean nextPermutation(int[] a) {
        int n = a.length;
        int i = n - 2;
        while (i >= 0 && a[i] >= a[i + 1]) i--;
        if (i < 0) return false;
        int j = n - 1;
        while (a[j] <= a[i]) j--;
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;

        for (int l = i + 1, r = n - 1; l < r; l++, r--) {
            tmp = a[l];
            a[l] = a[r];
            a[r] = tmp;
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        String studentCode = "B22DCAT108";
        String qCode = "mCLKBEFS";
        Registry registry = LocateRegistry.getRegistry("203.162.10.109", 1099);
        DataService service =
                (DataService) registry.lookup("RMIDataService");

        String data = (String) service.requestData(studentCode, qCode);
        String[] parts = data.replace(" ", "").split(",");
        int[] arr = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            arr[i] = Integer.parseInt(parts[i]);
        }
        boolean hasNext = nextPermutation(arr);
        if (!hasNext) {
            Arrays.sort(arr);
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            result.append(arr[i]);
            if (i < arr.length - 1) result.append(",");
        }
        service.submitData(studentCode, qCode, result.toString());
    }
}








Phan chia chan le

public class BS {
    public static void main(String[] args) throws Exception {
        String studentCode = "B22DCAT108";
        String qCode = "sVoPGIug";
        Registry registry = LocateRegistry.getRegistry("203.162.10.109", 1099);
        ByteService service = (ByteService) registry.lookup("RMIByteService");
        byte[] data = service.requestData(studentCode, qCode);
        List<Byte> even = new ArrayList<>();
        List<Byte> odd = new ArrayList<>();

        for (byte b : data) {
            if (b % 2 == 0) {
                even.add(b);
            } else {
                odd.add(b);
            }
        }
        byte[] result = new byte[data.length];
        int index = 0;
        for (byte b : even) {
            result[index++] = b;
        }
        for (byte b : odd) {
            result[index++] = b;
        }
        service.submitData(studentCode, qCode, result);
    }
}






Ma hoa ceasar

public class BS {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("203.162.10.109", 1099);
        ByteService service = (ByteService) registry.lookup("RMIByteService");
        String studentCode = "b22dcdt095";
        String qCode = "jdv4l1WQ";
        byte[] data = service.requestData(studentCode, qCode);

        int shift = data.length;

        byte[] encoded = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            encoded[i] = (byte) (data[i] + shift);
        }

        service.submitData(studentCode, qCode, encoded);
    }
}


Thu tu xuat hien cua cac ki tu trong chuoi

public class CS {
    public static void main(String[] args) throws Exception {
        String studentCode = "b22dcdt095";
        String qCode = "2839KtG5";
        Registry registry = LocateRegistry.getRegistry("203.162.10.109", 1099);
        CharacterService service = (CharacterService) registry.lookup("RMICharacterService");
        String input = service.requestCharacter(studentCode, qCode);

        Map<Character, Integer> map = new LinkedHashMap<>();
        for (char c : input.toCharArray()) {
            map.put(c, map.getOrDefault(c, 0) + 1);
        }

        StringBuilder result = new StringBuilder();
        for (Map.Entry<Character, Integer> e : map.entrySet()) {
            result.append(e.getKey()).append(e.getValue());
        }

        service.submitCharacter(studentCode, qCode, result.toString());
    }
}








