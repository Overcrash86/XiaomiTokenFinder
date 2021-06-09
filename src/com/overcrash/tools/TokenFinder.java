package com.overcrash.tools;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class TokenFinder
{

	public static void main(String[] args) throws Exception
	{
		File vBackup;

		if (args.length > 0)
		{
			vBackup = new File(args[0]);
		} else
		{
			vBackup = getLastBackup();
		}

		String miHomeSqliteFile = getMiHomeSqliteDBFile(vBackup);

		Collection<Token> vVacuums = getZtokens(miHomeSqliteFile);

		for (Token vaccum : vVacuums) {
			System.out.println("\n"+vaccum.vZname+" :\n"
					+ "Token : " + DecryptZtoken(vaccum.getvZtoken())+"\n"
					+ "Local IP : "+vaccum.getvZlocalIp()+"\n"
					+ "MAC : "+ vaccum.getvZmac()+"\n"
					+ "Model : "+vaccum.getvZmodel());
		}

	}

	public static File getLastBackup()
	{
		File dir = new File(System.getenv("APPDATA") + "\\Apple Computer\\MobileSync\\Backup");
		File[] files = dir.listFiles();
		File lastModified = Arrays.stream(files).filter(File::isDirectory).max(Comparator.comparing(File::lastModified)).orElse(null);
		// System.out.println(lastModified);
		return lastModified;
	}

	public static String getMiHomeSqliteDBFile(File backupPath)
	{

		System.out.println("Used Backup Folder : " + backupPath.getAbsolutePath());

		String vMiHomeSqliteFile = "";
		File vBackupSqliteDBFile = new File(backupPath.getAbsolutePath() + "\\Manifest.db");

		if (vBackupSqliteDBFile.exists())
		{

			// create a database connection
			Connection connection = null;
			try
			{
				connection = DriverManager.getConnection("jdbc:sqlite:" + vBackupSqliteDBFile.getAbsolutePath());
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(30); // set timeout to 30 sec.

				ResultSet rs = statement.executeQuery("select fileID from Files WHERE domain = 'AppDomain-com.xiaomi.mihome' and relativePath like '%mihome.sqlite'");
				if (rs.next())
				{
					// read the result set
					String fileID = rs.getString(1);
					vMiHomeSqliteFile = backupPath.getAbsolutePath() + "\\" + fileID.substring(0, 2) + "\\" + fileID;
					System.out.println(vMiHomeSqliteFile);
				} else
				{
					System.exit(-1);
				}
				statement.close();
				rs.close();
				connection.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
				System.exit(-1);
			}
			System.out.println("MiHome sqlite db : " + vMiHomeSqliteFile);

		} else
		{
			System.out.println("Unable to find valid Backup sqlite DB at " + vBackupSqliteDBFile.getAbsolutePath());
			System.exit(-1);
		}
		return vMiHomeSqliteFile;

	}

	public static Collection<Token> getZtokens(String aMiHomeSqliteFile)
	{
		Collection<Token> tokens = new ArrayList<Token>();
		
		if (new File(aMiHomeSqliteFile).exists())
		{

			Connection connectionMihomeSqlite = null;
			try
			{
				connectionMihomeSqlite = DriverManager.getConnection("jdbc:sqlite:" + aMiHomeSqliteFile);
				Statement statementMihomeSqlite = connectionMihomeSqlite.createStatement();
				statementMihomeSqlite.setQueryTimeout(30); // set timeout to 30 sec.

				ResultSet rsMihomeSqlite = statementMihomeSqlite.executeQuery("select ZTOKEN, ZLOCALIP, ZMAC, ZMODEL, ZNAME from ZDEVICE where ZMODEL like '%vacuum%'");

				while (rsMihomeSqlite.next())
				{
					Token token = new Token();
					token.setvZtoken(rsMihomeSqlite.getString("ZTOKEN"));
					token.setvZlocalIp(rsMihomeSqlite.getString("ZLOCALIP"));
					token.setvZmac(rsMihomeSqlite.getString("ZMAC"));
					token.setvZmodel(rsMihomeSqlite.getString("ZMODEL"));
					token.setvZname(rsMihomeSqlite.getString("ZNAME"));
					tokens.add(token);
				}
				
				statementMihomeSqlite.close();
				rsMihomeSqlite.close();
				connectionMihomeSqlite.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}

			System.out.println("Encrypted token : " + tokens);
		} else
		{
			System.out.println("Unable to find valid Xiaomi Home App sqlite DB at " + aMiHomeSqliteFile);
			System.exit(-1);
		}
		return tokens;
	}

	public static String DecryptZtoken(String aZtoken)
	{
		String finalToken = "";
		byte[] zskey = javax.xml.bind.DatatypeConverter.parseHexBinary("00000000000000000000000000000000");
		SecretKeySpec skey = new SecretKeySpec(zskey, "AES");

		char[] ch = aZtoken.toCharArray();
		try
		{

			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

			cipher.init(Cipher.DECRYPT_MODE, skey);

			finalToken = new String(cipher.doFinal(Hex.decodeHex(ch))).replaceAll("\\p{Cc}", ""); // Output as String

		} catch (Exception e)
		{
			System.out.println("Error while decrypting: " + e.toString());
			System.exit(-1);
		}

		return finalToken;
	}

}
