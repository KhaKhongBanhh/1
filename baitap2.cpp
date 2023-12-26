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
		void nhap();
		void xuat();
		int getTocDoDongCo(){
			return tocDoDongCo;
		}
};

void dongCo::nhap(){
	cout << "Nhap ten : ";
	getline(cin, ten);
	cout << "Nhap soxung : ";
	cin >> soXung;
	cout << "Nhap soxungvong : ";
	cin >> soXungVong;
	cout << "Nhap congsuat : ";
	cin >> congSuat;
	cout << "Nhap dienap : ";
	cin >> dienAp;
	
	soVong = soXung / soXungVong;
	tocDoDongCo =  soVong / thoiGian;
}

void dongCo::xuat(){
	cout << ten << " " << soXung << " " << soXungVong  << " " << dienAp <<endl;
}

bool cmp(dongCo a, dongCo b){
	return a.getTocDoDongCo() < b.getTocDoDongCo();
}

int main(){
	dongCo a[2];
	for(int i=0 ; i<2;i++){
		a[i].nhap();
		cin.ignore();
	}
	sort(a,a+2,cmp);
	for(int i=0 ; i<2;i++){
		a[i].xuat();
	}
}


