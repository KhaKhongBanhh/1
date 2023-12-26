#include<bits/stdc++.h>

using namespace std;

class dongCo{
	private :
		string ten;
		int soXung;
		int soXungVong;
		int congSuat;
		int dienAp;
		int thoiGian;
		int soVong; 		// soVong = soxung / soxungvong;
		int tocDoDongCo; 	// tocDoDongCo =  soVong / thoiGian;
	public :
		dongCo(){
		}
		dongCo(string ten, int soXung, int soXungVong, int congSuat, int dienAp, int thoiGian, int soVong, int tocDoDongCo){
			this -> ten = ten;
			this -> soXung = soXung;
			this -> soXungVong = soXungVong;
			this -> congSuat = congSuat;
			this -> dienAp = dienAp;
			this -> thoiGian = thoiGian;
			this -> soVong = soVong;
			this -> tocDoDongCo = tocDoDongCo;
		}
		int getTocDoDongCo(){
			return tocDoDongCo;
		}
		friend istream& operator >> (istream &in, dongCo &a);
		friend ostream& operator << (ostream &out, dongCo a);
};

istream& operator >> (istream &in, dongCo &a){
	cout << "Nhap ten : ";
	getline(in, a.ten);
	cout << "Nhap soxung : ";
	in >> a.soXung;
	cout << "Nhap soxungvong : ";
	in >> a.soXungVong;
	cout << "Nhap congsuat : ";
	in >> a.congSuat;
	cout << "Nhap dienap : ";
	in >> a.dienAp;
	
	a.soVong = a.soXung / a.soXungVong;
	a.tocDoDongCo =  a.soVong / a.thoiGian;
	return in;
}

ostream& operator << (ostream &out, dongCo a){
	out << a.ten << " " << a.soXung << " " << a.soXungVong  << " " << a.dienAp <<endl;
	return out;
}

int main(){
	dongCo a[2];
	
	for(int i=0;i<2;i++){
		cin >> a[i];
		cin.ignore();
	}
	for(int i=0;i<2;i++){
		cout << a[i];
	}
	return 0;
}
