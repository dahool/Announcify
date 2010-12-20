/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: aidl/ILicenseResultListener.aidl
 */
package com.android.vending.licensing;

import android.os.IBinder;

public interface ILicenseResultListener extends android.os.IInterface {
	/** Local-side IPC implementation stub class. */
	public static abstract class Stub extends android.os.Binder implements com.android.vending.licensing.ILicenseResultListener {
		private static final java.lang.String DESCRIPTOR = "com.android.vending.licensing.ILicenseResultListener";

		/** Construct the stub at attach it to the interface. */
		public Stub() {
			attachInterface(this, DESCRIPTOR);
		}

		/**
		 * Cast an IBinder object into an ILicenseResultListener interface,
		 * generating a proxy if needed.
		 */
		public static com.android.vending.licensing.ILicenseResultListener asInterface(final android.os.IBinder obj) {
			if (obj == null) {
				return null;
			}
			final android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
			if (iin != null && iin instanceof com.android.vending.licensing.ILicenseResultListener) {
				return (com.android.vending.licensing.ILicenseResultListener) iin;
			}
			return new com.android.vending.licensing.ILicenseResultListener.Stub.Proxy(obj);
		}

		public android.os.IBinder asBinder() {
			return this;
		}

		@Override
		public boolean onTransact(final int code, final android.os.Parcel data, final android.os.Parcel reply, final int flags) throws android.os.RemoteException {
			switch (code) {
				case INTERFACE_TRANSACTION: {
					reply.writeString(DESCRIPTOR);
					return true;
				}
				case TRANSACTION_verifyLicense: {
					data.enforceInterface(DESCRIPTOR);
					int _arg0;
					_arg0 = data.readInt();
					java.lang.String _arg1;
					_arg1 = data.readString();
					java.lang.String _arg2;
					_arg2 = data.readString();
					verifyLicense(_arg0, _arg1, _arg2);
					return true;
				}
			}
			return super.onTransact(code, data, reply, flags);
		}

		private static class Proxy implements com.android.vending.licensing.ILicenseResultListener {
			private final android.os.IBinder mRemote;

			Proxy(final android.os.IBinder remote) {
				mRemote = remote;
			}

			public android.os.IBinder asBinder() {
				return mRemote;
			}

			public java.lang.String getInterfaceDescriptor() {
				return DESCRIPTOR;
			}

			public void verifyLicense(final int responseCode, final java.lang.String signedData, final java.lang.String signature) throws android.os.RemoteException {
				final android.os.Parcel _data = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeInt(responseCode);
					_data.writeString(signedData);
					_data.writeString(signature);
					mRemote.transact(Stub.TRANSACTION_verifyLicense, _data, null, IBinder.FLAG_ONEWAY);
				} finally {
					_data.recycle();
				}
			}
		}

		static final int TRANSACTION_verifyLicense = IBinder.FIRST_CALL_TRANSACTION + 0;
	}

	public void verifyLicense(int responseCode, java.lang.String signedData, java.lang.String signature) throws android.os.RemoteException;
}