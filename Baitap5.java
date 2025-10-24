//// bytestream
//package thi_lap_trinh_mang_tcp;
//
//import java.util.*;
//import java.io.*;
//import java.net.*;
//
//public class Baitap1{
//	public static void main(String[] args) throws Exception{
//		Socket socket = new Socket("203.162.10.109", 2206);
//		InputStream in = socket.getInputStream();
//		OutputStream out = socket.getOutputStream();
//		
//		String code = "B22DCDT095;CHfqRv2Q";
//		out.write(code.getBytes());
//		out.flush();
//		
//		byte[] buffer = new byte[1024];
//		int len = in.read(buffer);
//		String s = new String(buffer, 0, len);
//		s = s.replace(',', ' ');
//		s = s.trim();
//		int n = Integer.parseInt(s);
//		
//		ArrayList<Integer> a = new ArrayList<>();
//		a.add(n);
//		while(n!=1) {
//			if(n % 2==0) n = n/2;
//			else n = 3*n + 1;
//			a.add(n);
//		}
//		
//		StringBuilder sb = new StringBuilder();
//		for(int i=0;i<a.size();i++) {
//			sb.append(a.get(i));
//			if(i < a.size()-1) sb.append(" ");
//		}
//		String res = sb.toString() + "; " + a.size();
//		out.write(res.getBytes());
//		out.flush();
//		
//		out.close();
//		in.close();
//		socket.close();
//	}
//}

//------------------------------------------------

//// tcp character stream
//package thi_lap_trinh_mang_tcp;
//
//import java.util.*;
//import java.net.*;
//import java.io.*;
//
//public class Baitap2{
//	public static void main(String[] args) throws Exception{
//		Socket socket = new Socket("203.162.10.109", 2208);
//		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//		
//		String code = "B22DCDT095;N0X1bzPa";
//		out.write(code);
//		out.newLine();
//		out.flush();
//		
//		String s = in.readLine();
//		s = s.trim();
//		
//		Map<Character, Integer> map = new LinkedHashMap<>();
//		for(char x : s.toCharArray())
//			if(Character.isLetterOrDigit(x))
//				map.put(x, map.getOrDefault(x, 0) + 1);
//		
//		StringBuilder sb = new StringBuilder();
//		for(Map.Entry<Character, Integer> e : map.entrySet())
//			if(e.getValue() > 1)
//				sb.append(e.getKey()).append(":").append(e.getValue()).append(",");
//		out.write(sb.toString());
//		out.flush();
//		
//		out.close();
//		in.close();
//		socket.close();
//	}
//}


//-----------------------------------------------------------------------

//// tcp data stream
//package thi_lap_trinh_mang_tcp;
//
//import java.util.*;
//import java.io.*;
//import java.net.*;
//
//public class Baitap3{
//	public static void main(String[] args) throws Exception{
//		Socket socket = new Socket("203.162.10.109", 2207);
//		DataInputStream in = new DataInputStream(socket.getInputStream());
//		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//		
//		String code = "B22DCDT095;jp6T4z7h";
//		out.writeUTF(code);
//		out.flush();
//		
//		int n = in.readInt();
//		String bin = Integer.toBinaryString(n);
//		String hex = Integer.toHexString(n).toUpperCase();
//		
//		String res = bin + ";" + hex;
//		out.writeUTF(res);
//		out.flush();
//		
//		out.close();
//		in.close();
//		socket.close();
//	}
//}



//------------------------------------------------

//package TCP;
//
//import java.io.Serializable;
//
//public class Customer implements Serializable{
//	private static final long serialVersionUID = 20170711L;
//	public int id;
//	public String code, name, dayOfBirth, username;
//	public int getId() {
//		return id;
//	}
//	public void setId(int id) {
//		this.id = id;
//	}
//	public String getCode() {
//		return code;
//	}
//	public void setCode(String code) {
//		this.code = code;
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getDayOfBirth() {
//		return dayOfBirth;
//	}
//	public void setDayOfBirth(String dayOfBirth) {
//		this.dayOfBirth = dayOfBirth;
//	}
//	public String getUsername() {
//		return username;
//	}
//	public void setUsername(String username) {
//		this.username = username;
//	}
//}



//package TCP;
//
//import java.util.*;
//import java.net.*;
//import java.io.*;
//
//public class mainCustomer {
//    public static void main(String[] args) throws Exception {
//        Socket socket = new Socket("203.162.10.109", 2209);
//        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
//        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
//
//        String code = "B22DCDT095;ykjsGrj9";
//        out.writeObject(code);
//        out.flush();
//
//        Customer customer = (Customer) in.readObject();
//
//        chuan(customer);
//
//        out.writeObject(customer);
//        out.flush();
//
//        out.close();
//        in.close();
//        socket.close();
//    }
//
//    public static void chuan(Customer c) {
//        String str = c.getName().trim();
//        String[] tmpten = str.split("\\s+");
//
//        String ten = tmpten[tmpten.length - 1].toUpperCase();
//        String ho = "";
//        for (int i = 0; i < tmpten.length - 1; i++) {
//            String p = tmpten[i].toLowerCase();
//            ho += Character.toUpperCase(p.charAt(0)) + p.substring(1) + " ";
//        }
//        ten += ", " + ho;
//        c.setName(ten);
//
//        String dob = c.getDayOfBirth();
//        String[] d = dob.split("-");
//        String newDob = d[1] + "/" + d[0] + "/" + d[2];
//        c.setDayOfBirth(newDob);
//        
//        String u = "";
//        for (int i = 0; i < tmpten.length - 1; i++) u += Character.toLowerCase(tmpten[i].charAt(0));
//        u += tmpten[tmpten.length - 1].toLowerCase();
//        c.setUserName(u);
//    }
//}





//----------------------------------------------------


// udp data type 

//package file_de_chay_java;
//
//import java.util.*;
//import java.io.*;
//import java.net.*;
//
//public class Baitap1{
//	public static void main(String[] args) throws Exception{
//		DatagramSocket socket = new DatagramSocket();
//		InetAddress sA = InetAddress.getByName("203.162.10.109");
//		int sP = 2207;
//		
//		String code = ";B22DCDT095;SMjXfGua";
//		DatagramPacket dpGui = new DatagramPacket(code.getBytes(), code.length(), sA, sP);
//		socket.send(dpGui);
//		
//		byte[] buffer = new byte[1024];
//		DatagramPacket dpNhan = new DatagramPacket(buffer, buffer.length);
//		socket.receive(dpNhan);
//		String s = new String(dpNhan.getData()).trim();
//		s = s.replace(';', ' ');
//		
//		String[] tmp = s.split("\\s+");
//		String rqID = tmp[0];
//		String numstr = tmp[1];
//		
//		long sum = 0;
//		for(char x : numstr.toCharArray())
//			if(Character.isDigit(x))
//				sum += Character.getNumericValue(x);
//		
//		String res = rqID + ";" + sum;
//		DatagramPacket dpGui1 = new DatagramPacket(res.getBytes(), res.length(), sA, sP);
//		socket.send(dpGui1);
//
//		socket.close();
//	}
//}






//------------------------------------------------------------------

//tcp String

//package file_de_chay_java;
//
//import java.net.*;
//
//public class Baitap2 {
//    public static void main(String[] args) throws Exception {
//        DatagramSocket socket = new DatagramSocket();
//        InetAddress sA = InetAddress.getByName("203.162.10.109");
//        int sP = 2208;
//
//        String code = ";B22DCDT095;BxH7k7Lr";
//        DatagramPacket dpGui = new DatagramPacket(code.getBytes(), code.length(), sA, sP);
//        socket.send(dpGui);
//
//        byte[] buffer = new byte[1024];
//        DatagramPacket dpNhan = new DatagramPacket(buffer, buffer.length);
//        socket.receive(dpNhan);
//        String s = new String(dpNhan.getData()).trim();
//        s = s.replace(';', ' ');
//        s = s.replace(',', ' ');
//        s = s.trim();
//        String[] tmp = s.split("\\s+");
//
//        String rqId = tmp[0];
//        String b1 = tmp[1];
//        String b2 = tmp[2];
//        long num1 = Long.parseLong(b1, 2);
//        long num2 = Long.parseLong(b2, 2);
//        long sum = num1 + num2;
//
//        String res = rqId + ";" + sum;
//        DatagramPacket dpGui1 = new DatagramPacket(res.getBytes(), res.length(), sA, sP);
//        socket.send(dpGui1);
//
//        socket.close();
//    }
//}






//-------------------------------------------------

// RMI mainbyte
//package RMI;
//
//import java.rmi.registry.LocateRegistry;
//import java.rmi.registry.Registry;
//import java.util.Arrays;
//import java.nio.charset.StandardCharsets;
//
//public class mainbyte {
//    public static void main(String[] args) throws Exception{
//            Registry registry = LocateRegistry.getRegistry("203.162.10.109", 1099);
//            ByteService service = (ByteService) registry.lookup("RMIByteService");
//
//            String studentCode = "B22DCDT095";
//            String qCode = "uLiuk5G0";
//
//            byte[] data = service.requestData(studentCode, qCode);
//            String asIso = new String(data, StandardCharsets.ISO_8859_1);
//
//            int[] unsignedOrig = new int[data.length];
//            for (int i = 0; i < data.length; i++) {
//                unsignedOrig[i] = data[i] & 0xFF;
//            }
//
//            int shift = data.length;
//            byte[] encoded = new byte[data.length];
//            int[] encodedUnsigned = new int[data.length];
//            for (int i = 0; i < data.length; i++) {
//                int unsigned = data[i] & 0xFF;
//                int shifted = (unsigned + shift) % 256; // <-- QUAN TRỌNG: %256, không %128
//                encodedUnsigned[i] = shifted;
//                encoded[i] = (byte) shifted;
//            }
//            
//            service.submitData(studentCode, qCode, encoded);
//    }
//}



//--------------------------------------------------------

// RMI main character
//package RMI;
//
//import java.rmi.registry.LocateRegistry;
//import java.rmi.registry.Registry;
//import java.util.Arrays;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//public interface maincharacter {
//	public static void main(String[] args) throws Exception {
//        String host = "203.162.10.109"; // Địa chỉ máy chủ (cập nhật đúng theo đề)
//        int port = 1099; // Cổng mặc định của RMI Registry
//        Registry registry = LocateRegistry.getRegistry(host, port);
//        CharacterService service = (CharacterService) registry.lookup("RMICharacterService");
//        String studentCode = "B22DCDT095";
//        String qCode = "QKgTLCXq";
//        String input = service.requestCharacter(studentCode, qCode);
//
//        Map<Character, Integer> freq = new LinkedHashMap<>();
//        for (char c : input.toCharArray()) {
//            freq.put(c, freq.getOrDefault(c, 0) + 1);
//        }
//
//        StringBuilder result = new StringBuilder();
//        result.append("{");
//        boolean first = true;
//        for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
//            if (!first) result.append(", ");
//            result.append("\"").append(entry.getKey()).append("\": ").append(entry.getValue());
//            first = false;
//        }
//        result.append("}");
//
//        service.submitCharacter(studentCode, qCode, result.toString());
//	}
//}




//--------------------------------------------------------

// rmi data
//package RMI;
//
//import java.rmi.registry.LocateRegistry;
//import java.rmi.registry.Registry;
//import java.util.Arrays;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//public interface maindata {
//	public static void main(String[] args) throws Exception{
//		 String studentCode = "B22DCDT095";
//         String qCode = "eiVziIca";
//         Registry registry = LocateRegistry.getRegistry("203.162.10.109", 1099);
//         DataService service = (DataService) registry.lookup("RMIDataService");
//         Object response = service.requestData(studentCode, qCode);
//         int amount = Integer.parseInt(response.toString());
//         
//         int[] coins = {10, 5, 2, 1};
//         int remaining = amount;
//         StringBuilder usedCoins = new StringBuilder();
//         int count = 0;
//
//         for (int coin : coins) {
//             while (remaining >= coin) {
//                 remaining -= coin;
//                 usedCoins.append(coin).append(",");
//                 count++;
//             }
//         }
//
//         String result;
//         if (remaining != 0) {
//             result = "-1"; // Không thể tạo được số tiền mong muốn
//         } else {
//             if (usedCoins.length() > 0) usedCoins.deleteCharAt(usedCoins.length() - 1);
//             result = count + "; " + usedCoins.toString();
//         }
//         service.submitData(studentCode, qCode, result);
//	}
//}


