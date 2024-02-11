CREATE TABLE KHACHHANG
(
	MAKH		char(4) PRIMARY KEY,
	HOTEN	varchar(40),
	DCHI		varchar(50),
	SODT		varchar(20),
	NGSINH	smalldatetime,
	NGDK		smalldatetime,
	DOANHSO	money
)
CREATE TABLE NHANVIEN
(
	MANV		char(4)PRIMARY KEY,
	HOTEN	varchar(40),
	SODT		varchar(20),
	NGVL		smalldatetime
)
CREATE TABLE SANPHAM
(
	MASP		char(4)PRIMARY KEY,
	TENSP	varchar(40),
	DVT		varchar(20),
	NUOCSX	varchar(40),
	GIA		money
)
CREATE TABLE HOADON
(
	SOHD		int PRIMARY KEY,
	NGHD		smalldatetime,
	MAKH		char(4) FOREIGN KEY REFERENCES KHACHHANG(MAKH),
	MANV		char(4) FOREIGN KEY REFERENCES NHANVIEN(MANV),
	TRIGIA	money
)
CREATE TABLE CTHD
(
	SOHD		int FOREIGN KEY REFERENCES HOADON(SOHD),
	MASP		char(4) FOREIGN KEY REFERENCES SANPHAM(MASP),
	SL		int,
	CONSTRAINT PK_CTHD PRIMARY KEY (SOHD,MASP)
)
--I:DINH NGHIA DU LIEU
ALTER TABLE SANPHAM ADD GHICHU VARCHAR(20)
ALTER TABLE KHACHHANG ADD LOAIKH TINYINT 
ALTER TABLE SANPHAM ALTER COLUMN GHICHU VARCHAR(100)
ALTER TABLE SANPHAM DROP COLUMN GHICHU
ALTER TABLE KHACHHANG ALTER COLUMN LOAIKH VARCHAR(50)
ALTER TABLE SANPHAM ADD CONSTRAINT CHECK_DVT CHECK(DVT='CAY'OR DVT='CAI'OR DVT='HOP'OR DVT='QUYEN'OR DVT='CHUC')
ALTER TABLE SANPHAM ADD CONSTRAINT CHECK_GIA CHECK(GIA>=500)
ALTER TABLE KHACHHANG ADD CONSTRAINT CHECK_NGDK CHECK (NGDK>NGSINH)
 /*CAU 11*/
CREATE TRIGGER UPDATE_KH_C11
ON KHACHHANG
FOR UPDATE
AS
	DECLARE	@NGDK	SMALLDATETIME,	
			@NGHD	SMALLDATETIME

	SELECT	@NGDK=NGDK
	FROM		INSERTED
	
	IF(@NGDK>ANY(SELECT	NGHD
				FROM		HOADON A, INSERTED I
				WHERE	A.MAKH=I.MAKH))
		BEGIN
			ROLLBACK TRAN
			PRINT 'ERROR!NGDK PHAI NHO HON NGHD'
		END
	ELSE
		PRINT' SUCCESSFUL'
	-------
CREATE TRIGGER HD_C11
ON HOADON
FOR INSERT,UPDATE
AS
	DECLARE	@NGDK	SMALLDATETIME,	
			@NGHD	SMALLDATETIME

	SELECT	@NGDK=NGDK,@NGHD=NGHD
	FROM		INSERTED I, KHACHHANG A
	WHERE	I.MAKH=A.MAKH

	IF @NGHD<@NGDK
		BEGIN
			ROLLBACK TRAN
			PRINT 'ERROR!NGHD PHAI LON HON NGDK'
		END
	ELSE
		PRINT' SUCCESSFUL'
/*CAU 12*/
CREATE TRIGGER UPDATE_NV_C12
ON NHANVIEN
FOR UPDATE
AS
	DECLARE	@NGVL	SMALLDATETIME,	
			@NGHD	SMALLDATETIME

	SELECT	@NGVL=NGVL
	FROM		INSERTED
	
	IF(@NGVL>ANY(SELECT	NGHD
				FROM		HOADON A, INSERTED I
				WHERE	A.MANV=I.MANV))
		BEGIN
			ROLLBACK TRAN
			PRINT 'ERROR!NGVL PHAI NHO HON NGHD'
		END
	ELSE
		PRINT' SUCCESSFUL'
	-------
CREATE TRIGGER HD_C12
ON HOADON
FOR INSERT,UPDATE
AS
	DECLARE	@NGVL	SMALLDATETIME,	
			@NGHD	SMALLDATETIME

	SELECT	@NGVL=NGVL,@NGHD=NGHD
	FROM		INSERTED I, NHANVIEN A
	WHERE	I.MANV=A.MANV

	IF @NGHD<@NGVL
		BEGIN
			ROLLBACK TRAN
			PRINT 'ERROR!NGHD PHAI LON HON NGVL'
		END
	ELSE
		PRINT' SUCCESSFUL'
/*CAU 13*/
CREATE TRIGGER CTHD_C13
ON	CTHD
FOR	DELETE,UPDATE
AS
	DECLARE	@SL		INT,
			@SOHD	INT

	SELECT	@SL=COUNT(A.SOHD),@SOHD=D.SOHD
	FROM		DELETED D,CTHD A
	WHERE	A.SOHD=D.SOHD
	GROUP BY  D.SOHD

	IF(@SL<1)
		BEGIN
			DELETE FROM	HOADON
			WHERE		SOHD=@SOHD
			PRINT 'DA DELETE CTHD CUOI CUNG CUA HOADON TREN'
		END 
	------
CREATE TRIGGER HOADON_C13
ON	HOADON
FOR	INSERT
AS
	DECLARE	@SOHD	INT

	SELECT	@SOHD=SOHD
	FROM		INSERTED
	
	UPDATE	CTHD
	SET		MASP='NONE',SL=0
	WHERE	SOHD=@SOHD

	PRINT 'SUCCESSFUL! DE NGHI UPDATE LAI CTHD(MAC DINH:MASP="NONE", SL=0)'
/*CAU 14*/
CREATE TRIGGER INSERT_HOADON_C14
ON HOADON
FOR	INSERT
AS
	UPDATE	HOADON
	SET		TRIGIA=0
	WHERE	SOHD=(SELECT 	SOHD
				FROM		INSERTED)
	PRINT'DA INSERT 1 HOADON VOI TRIGIA BAN DAU LA 0 VND'	
	-------------
CREATE TRIGGER UPDATE_HOADON_C14
ON HOADON
FOR	INSERT
AS
	UPDATE	HOADON
	SET		TRIGIA=(SELECT TRIGIA
					FROM	DELETED)
	WHERE	SOHD=(SELECT 	SOHD
				FROM		INSERTED)
	PRINT'DA UPDATE 1 HOADON VOI TRIGIA KHONG THAY DOI'
	-------------
CREATE TRIGGER INSERT_CTHD_C14
ON	CTHD
FOR	INSERT
AS
	DECLARE 	@SL		INT,
			@GIA		MONEY,
			@SOHD	INT

	SELECT	@GIA=GIA,@SL=SL,@SOHD=SOHD
	FROM		INSERTED A, SANPHAM B
	WHERE	A.MASP=B.MASP

	UPDATE	HOADON
	SET		TRIGIA=TRIGIA+@SL*@GIA
		PRINT'DA INSERT 1 CTHD VA UPDATE LAI TRIGIA CUA HOADON TUONG UNG'
	--------------
CREATE TRIGGER DELETE_CTHD_C14
ON	CTHD
FOR	DELETE
AS
	DECLARE 	@SL		INT,
			@GIA		MONEY,
			@SOHD	INT

	SELECT	@GIA=GIA,@SL=SL,@SOHD=SOHD
	FROM		DELETED A, SANPHAM B
	WHERE	A.MASP=B.MASP

	UPDATE	HOADON
	SET		TRIGIA=TRIGIA-@SL*@GIA
		PRINT'DA DELETE 1 CTHD VA UPDATE LAI TRIGIA CUA HOADON TUONG UNG'
	-------------------
CREATE TRIGGER UPDATE_CTHD_C14
ON	CTHD
FOR	UPDATE
AS
	DECLARE 	@SL_CU	INT,
			@SL_MOI	INT,			
			@GIA_CU	MONEY,
			@GIA_MOI	MONEY,
			@SOHD_CU	INT,
			@SOHD_MOI	INT

	SELECT	@GIA_CU=GIA,@SL_CU=SL,@SOHD_CU=SOHD
	FROM		DELETED A, SANPHAM B
	WHERE	A.MASP=B.MASP
	
	SELECT	@GIA_MOI=GIA,@SL_MOI=SL,@SOHD_MOI=SOHD
	FROM		INSERTED A, SANPHAM B
	WHERE	A.MASP=B.MASP

	IF(@SOHD_CU=@SOHD_MOI)
		BEGIN
			UPDATE	HOADON
			SET		TRIGIA=TRIGIA+@SL_MOI*@GIA_MOI-@SL_CU*@GIA_CU
			WHERE	SOHD=@SOHD_CU
		END
	ELSE
		BEGIN
			UPDATE	HOADON
			SET		TRIGIA=TRIGIA-@SL_CU*@GIA_CU
			WHERE	SOHD=@SOHD_CU

			UPDATE	HOADON
			SET		TRIGIA=TRIGIA+@SL_MOI*@GIA_MOI
			WHERE	SOHD=@SOHD_MOI
		END
	PRINT'DA UPDATE 1 CTHD VA UPDATE LAI TRIGIA CUA HOADON TUONG UNG'
	-------------------
CREATE	TRIGGER UPDATE_HOADON_C14
ON		HOADON
FOR		INSERT
AS
	DECLARE	@GIA_CU	MONEY,
			@GIA_MOI	MONEY,
			@SOHD	INT,
			@SL		INT
			
	SELECT	@GIA_CU=GIA
	FROM		DELETED
	SELECT	@GIA_MOI=GIA
	FROM		INSERTED

	SELECT	@SOHD=SOHD,@SL=SL
	FROM		INSERTED A, CTHD B
	WHERE	A.MASP=B.MASP

	UPDATE	HOADON
	SET		TRIGIA=TRIGIA+@SL*(@GIA_MOI-@GIA_CU)	
	WHERE	SOHD=@SOHD
		PRINT'DA UPDATE 1 HOADON VOI TRIGIA KHONG THAY DOI'
/*CAU 15*/
CREATE TRIGGER INSERT_KHACHHANG_C15
ON KHACHHANG
FOR	INSERT
AS
	DECLARE	@MAKH	CHAR(4)

	SELECT	@MAKH=MAKH
	FROM		INSERTED
	
	UPDATE	KHACHHANG
	SET		DOANHSO=0
	WHERE	MAKH=@MAKH

	PRINT 'DA INSERT 1 KHACHHANG MOI VOI DOANHSO BAN DAU LA 0 VND'
	----------------
CREATE TRIGGER UPDATE_KHACHHANG_C15
ON KHACHHANG
FOR	UPDATE
AS
	DECLARE	@MAKH		CHAR(4),
			@DOANHSO_CU	MONEY

	SELECT	@MAKH=MAKH
	FROM		INSERTED
	
	SELECT	@DOANHSO_CU=DOANHSO
	FROM		DELETED
	
	UPDATE	KHACHHANG
	SET		DOANHSO=@DOANHSO_CU
	WHERE	MAKH=@MAKH

	PRINT 'DA UPDATE 1 KHACHHANG'
	----------------
CREATE TRIGGER INSERT_HOADON_C15
ON HOADON
FOR	INSERT
AS
	DECLARE	@TRIGIA	MONEY,
			@MAKH	CHAR(4)

	SELECT	@MAKH=MAKH,@TRIGIA=TRIGIA
	FROM		INSERTED
	
	UPDATE	KHACHHANG
	SET		DOANHSO=DOANHSO+@TRIGIA
	WHERE	MAKH=@MAKH

	PRINT 'DA INSERT 1 HODON MOI VA UPDATE LAI DOANHSO CUA KH CO SOHD TREN'

	-----------
CREATE TRIGGER DELETE_HOADON_C15
ON HOADON
FOR	DELETE
AS
	DECLARE	@TRIGIA	MONEY,
			@MAKH	CHAR(4)

	SELECT	@MAKH=MAKH,@TRIGIA=TRIGIA
	FROM		DELETED
	
	UPDATE	KHACHHANG
	SET		DOANHSO=DOANHSO-@TRIGIA
	WHERE	MAKH=@MAKH

	PRINT 'DA DELETE 1 HODON MOI VA UPDATE LAI DOANHSO CUA KH CO SOHD TREN'
	---------------
CREATE TRIGGER UPDATE_HOADON_C15
ON HOADON
FOR	UPDATE
AS
	DECLARE	@TRIGIA_CU	MONEY,
			@TRIGIA_MOI	MONEY,
			@MAKH		CHAR(4)

	SELECT	@MAKH=MAKH,@TRIGIA_MOI=TRIGIA
	FROM		INSERTED

	SELECT	@MAKH=MAKH,@TRIGIA_CU=TRIGIA
	FROM		DELETED
		
	UPDATE	KHACHHANG
	SET		DOANHSO=DOANHSO+@TRIGIA_MOI-@TRIGIA_CU
	WHERE	MAKH=@MAKH

	PRINT 'DA UPDATE 1 HOADON MOI VA UPDATE LAI DOANHSO CUA KH CO SOHD TREN'
--------------------------TRUY VAN DU LIEU-----------------------------------------------------------------
--CAU 1)
SELECT	MASP,TENSP
FROM		SANPHAM
WHERE	NUOCSX='TRUNG QUOC'
--CAU 2)
SELECT	MASP,TENSP
FROM		SANPHAM
WHERE	DVT='CAY' OR DVT='QUYEN'
--CAU 3)
SELECT	MASP,TENSP
FROM		SANPHAM
WHERE	MASP LIKE 'B%01'
--CAU 4)
SELECT	MASP,TENSP
FROM		SANPHAM
WHERE	NUOCSX='TRUNG QUOC' AND GIA BETWEEN 30000 AND 40000
--CAU 5)
SELECT	MASP,TENSP
FROM		SANPHAM
WHERE	(NUOCSX='TRUNG QUOC' OR NUOCSX='THAI LAN') AND GIA BETWEEN 30000 AND 40000
--CAU 6)
SELECT	SOHD,TRIGIA
FROM		HOADON
WHERE	NGHD='1/1/2007' OR NGHD='2/1/2007'
--CAU 7)
SELECT	SOHD,TRIGIA
FROM		HOADON
WHERE	MONTH(NGHD)=1 AND YEAR(NGHD)=2007
ORDER BY  NGHD ASC,TRIGIA DESC
--CAU 8)
SELECT	A.MAKH,HOTEN
FROM		HOADON A, KHACHHANG B
WHERE	A.MAKH=B.MAKH AND NGHD='1/1/2007'
--CAU 9)
SELECT	SOHD,TRIGIA
FROM		HOADON A, NHANVIEN B
WHERE	A.MANV=B.MANV AND NGHD='28/10/2006' AND HOTEN='NGUYEN VAN B' 
--CAU 10)
SELECT	C.MASP, TENSP
FROM		HOADON A, KHACHHANG B, CTHD C, SANPHAM D
WHERE	A.MAKH=B.MAKH AND A.SOHD=C.SOHD AND C.MASP=D.MASP AND
		MONTH(NGHD)=10 AND YEAR(NGHD)=2006 AND HOTEN='NGUYEN VAN A' 
--CAU 11)
SELECT	SOHD
FROM		CTHD
WHERE	MASP='BB01'OR MASP='BB02'
--CAU 12)
SELECT	SOHD
FROM		CTHD
WHERE	(MASP='BB01'OR MASP='BB02') AND SL BETWEEN 10 AND 20
--CAU 13)
SELECT	SOHD
FROM		CTHD
WHERE	SL BETWEEN 10 AND 20 AND MASP='BB01'
		AND SOHD IN ( 	SELECT	SOHD
					FROM		CTHD
					WHERE	MASP='BB02')
--CAU 14)
SELECT	DISTINCT A.MASP,TENSP
FROM		SANPHAM A, HOADON B, CTHD C
WHERE	NUOCSX='TRUNG QUOC' OR
		(B.SOHD=C.SOHD AND C.MASP=A.MASP AND NGHD='1/1/2007') 
--CAU 15)
SELECT	MASP, TENSP
FROM		SANPHAM
WHERE	MASP NOT IN (	SELECT	MASP
					FROM		CTHD)
--CAU 16)
SELECT	MASP, TENSP
FROM		SANPHAM
WHERE	MASP NOT IN (	SELECT	A.MASP
					FROM		CTHD A, HOADON B
					WHERE	A.SOHD=B.SOHD AND YEAR(NGHD)=2006)
--CAU 17)
SELECT	MASP, TENSP
FROM		SANPHAM
WHERE	NUOCSX='TRUNG QUOC' AND
		MASP NOT IN (	SELECT	A.MASP
					FROM		CTHD A, HOADON B
					WHERE	A.SOHD=B.SOHD AND YEAR(NGHD)=2006)
--CAU 18)
SELECT	DISTINCT SOHD
FROM		CTHD A
WHERE	NOT EXISTS(SELECT	*
				FROM		SANPHAM B
				WHERE	NUOCSX='SINGAPORE' AND
						NOT EXISTS(SELECT	*
								FROM		CTHD C
								WHERE	C.MASP=B.MASP AND C.SOHD=A.SOHD))
--CAU 19)
--CAU 20)
SELECT	COUNT(SOHD)
FROM		HOADON
WHERE	MAKH IS NULL
--CAU 21)
SELECT	COUNT(DISTINCT MASP)
FROM		HOADON A, CTHD B
WHERE	A.SOHD=B.SOHD AND YEAR(NGHD)=2006
--CAU 22)
SELECT	MAX(TRIGIA) [TRI GIA CAO NHAT],MIN(TRIGIA) [TRI GIA THAP NHAT] 
FROM		HOADON
--CAU 23)
SELECT	AVG(TRIGIA)
FROM		HOADON
WHERE	YEAR(NGHD)=2006
--CAU 24)
SELECT	SUM(TRIGIA) [DOANH THU]
FROM		HOADON
WHERE	YEAR(NGHD)=2006
--CAU 27)
SELECT	*
FROM		KHACHHANG
WHERE	DOANHSO IN(SELECT TOP 3 DOANHSO
				FROM			KHACHHANG
				ORDER BY 		DOANHSO DESC) 	
--CAU 28)
SELECT	*
FROM		SANPHAM
WHERE	GIA IN(SELECT TOP 3 	GIA
				FROM			SANPHAM
				ORDER BY 		GIA DESC) 	
--CAU 29)
SELECT	*
FROM		SANPHAM
WHERE	NUOCSX='THAI LAN' AND GIA IN(SELECT TOP 3 	GIA
								FROM			SANPHAM
								ORDER BY 		GIA DESC) 
--CAU 30)
SELECT	*
FROM		SANPHAM
WHERE	NUOCSX='TRUNG QUOC' AND GIA IN(SELECT TOP 3 	GIA
								FROM			SANPHAM
								WHERE		NUOCSX='TRUNG QUOC'
								ORDER BY 		GIA DESC) 
--CAU 31)
SELECT	*
FROM		KHACHHANG
WHERE	DOANHSO IN(SELECT TOP 3 DOANHSO
				FROM			KHACHHANG
				ORDER BY 		DOANHSO DESC) 	
ORDER BY	DOANHSO DESC
--CAU 32)
SELECT	COUNT(MASP)
FROM		SANPHAM
WHERE	NUOCSX='TRUNG QUOC'
--CAU 33)
SELECT	NUOCSX,COUNT(MASP) [SO SAN PHAM]
FROM		SANPHAM
GROUP BY  NUOCSX
--CAU 34)
SELECT	NUOCSX,MIN(GIA) [GIA THAP NHAT], AVG(GIA) [GIA TB], MAX(GIA) [GIA CAO NHAT]
FROM		SANPHAM
GROUP BY  NUOCSX
--CAU 35)
SELECT	NGHD,SUM(TRIGIA) [DOANH THU]
FROM		HOADON
GROUP BY  NGHD
--CAU 36)
SELECT	MASP,SUM(SL) [TONG SO LUONG]
FROM		HOADON A, CTHD B
WHERE	A.SOHD=B.SOHD AND MONTH(NGHD)=10 AND YEAR(NGHD)=2006 
GROUP BY	MASP	
--CAU 37)
SELECT	MONTH(NGHD) THANG,SUM(TRIGIA) [DOANH THU]
FROM		HOADON A, CTHD B
WHERE	A.SOHD=B.SOHD AND YEAR(NGHD)=2006 
GROUP BY	MONTH(NGHD)
--CAU 38)
SELECT	*
FROM		HOADON
WHERE	SOHD IN (SELECT	SOHD
				FROM		CTHD
				GROUP BY	SOHD
				HAVING	COUNT(DISTINCT MASP)>=4)
--CAU 39)
SELECT	*
FROM		HOADON
WHERE	SOHD IN (SELECT	SOHD
				FROM		CTHD A, SANPHAM B
				WHERE	A.MASP=B.MASP AND NUOCSX='VIET NAM'
				GROUP BY	SOHD
				HAVING	COUNT(DISTINCT A.MASP)=3)
--CAU 40)
SELECT	*
FROM		KHACHHANG
WHERE	MAKH IN (SELECT	MAKH
				FROM		HOADON
				GROUP BY	MAKH
				HAVING	COUNT(SOHD)>=ALL(SELECT		COUNT(SOHD)
										FROM		HOADON
										GROUP BY	MAKH))
--CAU 41)
SELECT	MONTH(NGHD) THANG
FROM		HOADON
WHERE	YEAR(NGHD)=2006
GROUP BY	MONTH(NGHD)
HAVING	SUM(TRIGIA)>=ALL(SELECT	SUM(TRIGIA)
					FROM		HOADON
					WHERE	YEAR(NGHD)=2006
					GROUP BY	MONTH(NGHD))
--CAU 42)
SELECT	B.MASP, TENSP
FROM		SANPHAM A,CTHD B, HOADON C
WHERE	A.MASP=B.MASP AND B.SOHD=C.SOHD AND YEAR(NGHD)=2006
GROUP BY	B.MASP,TENSP
HAVING	SUM(SL)>=ALL(SELECT		SUM(SL)
					FROM		CTHD A, HOADON B
					WHERE	A.SOHD=B.SOHD AND YEAR(NGHD)=2006
					GROUP BY	MASP)
--CAU 43)
SELECT	NUOCSX,MASP, TENSP
FROM		SANPHAM A
WHERE	GIA=(SELECT	MAX(GIA)
			FROM		SANPHAM B
			WHERE	A.NUOCSX=B.NUOCSX)
GROUP BY	NUOCSX,MASP,TENSP
--CAU 44)
SELECT	NUOCSX
FROM		SANPHAM
GROUP BY	NUOCSX
HAVING	COUNT(DISTINCT GIA)>=3
--CAU 45)
SELECT	*
FROM		KHACHHANG
WHERE	MAKH IN (SELECT	A.MAKH
				FROM		HOADON A, KHACHHANG B
				WHERE	A.MAKH=B.MAKH AND
						DOANHSO IN (SELECT TOP 10 DOANHSO
								FROM		KHACHHANG
								ORDER BY DOANHSO DESC)	
				GROUP BY	A.MAKH
				HAVING	COUNT(SOHD)>=ALL(SELECT		COUNT(SOHD)
										FROM		HOADON A, KHACHHANG B
										WHERE	A.MAKH=B.MAKH AND
												DOANHSO IN (SELECT TOP 10 DOANHSO
															FROM		KHACHHANG
															ORDER BY DOANHSO DESC)		
										GROUP BY	A.MAKH))
















	