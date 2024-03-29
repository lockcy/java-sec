//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alibaba.com.caucho.hessian.io;

import com.alibaba.com.caucho.hessian.util.IdentityIntMap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class Hessian2Output extends AbstractHessianOutput implements Hessian2Constants {
    public static final int SIZE = 4096;
    private final byte[] _buffer = new byte[4096];
    protected OutputStream _os;
    private IdentityIntMap _refs = new IdentityIntMap();
    private boolean _isCloseStreamOnClose;
    private HashMap _classRefs;
    private HashMap _typeRefs;
    private int _offset;
    private boolean _isStreaming;

    public Hessian2Output(OutputStream os) {
        this._os = os;
    }

    public void init(OutputStream os) {
        this.reset();
        this._os = os;
    }

    public void reset() {
        this.resetReferences();
        if (this._classRefs != null) {
            this._classRefs.clear();
        }

        if (this._typeRefs != null) {
            this._typeRefs.clear();
        }

        this._offset = 0;
    }

    public boolean isCloseStreamOnClose() {
        return this._isCloseStreamOnClose;
    }

    public void setCloseStreamOnClose(boolean isClose) {
        this._isCloseStreamOnClose = isClose;
    }

    public void call(String method, Object[] args) throws IOException {
        int length = args != null ? args.length : 0;
        this.startCall(method, length);

        for (int i = 0; i < args.length; ++i) {
            this.writeObject(args[i]);
        }

        this.completeCall();
    }

    public void startCall(String method, int length) throws IOException {
        int offset = this._offset;
        if (4096 < offset + 32) {
            this.flush();
            offset = this._offset;
        }

        byte[] buffer = this._buffer;
        buffer[this._offset++] = 67;
        this.writeString(method);
        this.writeInt(length);
    }

    public void startCall() throws IOException {
        this.flushIfFull();
        this._buffer[this._offset++] = 67;
    }

    public void startEnvelope(String method) throws IOException {
        int offset = this._offset;
        if (4096 < offset + 32) {
            this.flush();
            offset = this._offset;
        }

        this._buffer[this._offset++] = 69;
        this.writeString(method);
    }

    public void completeEnvelope() throws IOException {
        this.flushIfFull();
        this._buffer[this._offset++] = 90;
    }

    public void writeMethod(String method) throws IOException {
        this.writeString(method);
    }

    public void completeCall() throws IOException {
    }

    public void startReply() throws IOException {
        this.writeVersion();
        this.flushIfFull();
        this._buffer[this._offset++] = 82;
    }

    public void writeVersion() throws IOException {
        this.flushIfFull();
        this._buffer[this._offset++] = 72;
        this._buffer[this._offset++] = 2;
        this._buffer[this._offset++] = 0;
    }

    public void completeReply() throws IOException {
    }

    public void startMessage() throws IOException {
        this.flushIfFull();
        this._buffer[this._offset++] = 112;
        this._buffer[this._offset++] = 2;
        this._buffer[this._offset++] = 0;
    }

    public void completeMessage() throws IOException {
        this.flushIfFull();
        this._buffer[this._offset++] = 122;
    }

    public void writeFault(String code, String message, Object detail) throws IOException {
        this.flushIfFull();
        this.writeVersion();
        this._buffer[this._offset++] = 70;
        this._buffer[this._offset++] = 72;
        this._refs.put(new HashMap(), this._refs.size());
        this.writeString("code");
        this.writeString(code);
        this.writeString("message");
        this.writeString(message);
        if (detail != null) {
            this.writeString("detail");
            this.writeObject(detail);
        }

        this.flushIfFull();
        this._buffer[this._offset++] = 90;
    }

    public void writeObject(Object object) throws IOException {
        if (object == null) {
            this.writeNull();
        } else {
            Serializer serializer = this.findSerializerFactory().getSerializer(object.getClass());
            serializer.writeObject(object, this);
        }
    }

    public boolean writeListBegin(int length, String type) throws IOException {
        this.flushIfFull();
        if (length < 0) {
            if (type != null) {
                this._buffer[this._offset++] = 85;
                this.writeType(type);
            } else {
                this._buffer[this._offset++] = 87;
            }

            return true;
        } else if (length <= 7) {
            if (type != null) {
                this._buffer[this._offset++] = (byte) (112 + length);
                this.writeType(type);
            } else {
                this._buffer[this._offset++] = (byte) (120 + length);
            }

            return false;
        } else {
            if (type != null) {
                this._buffer[this._offset++] = 86;
                this.writeType(type);
            } else {
                this._buffer[this._offset++] = 88;
            }

            this.writeInt(length);
            return false;
        }
    }

    public void writeListEnd() throws IOException {
        this.flushIfFull();
        this._buffer[this._offset++] = 90;
    }

    public void writeMapBegin(String type) throws IOException {
        if (4096 < this._offset + 32) {
            this.flush();
        }

        if (type != null) {
            this._buffer[this._offset++] = 77;
            this.writeType(type);
        } else {
            this._buffer[this._offset++] = 72;
        }

    }

    public void writeMapEnd() throws IOException {
        if (4096 < this._offset + 32) {
            this.flush();
        }

        this._buffer[this._offset++] = 90;
    }

    public int writeObjectBegin(String type) throws IOException {
        if (this._classRefs == null) {
            this._classRefs = new HashMap();
        }

        Integer refV = (Integer) this._classRefs.get(type);
        int ref;
        if (refV != null) {
            ref = refV;
            if (4096 < this._offset + 32) {
                this.flush();
            }

            if (ref <= 15) {
                this._buffer[this._offset++] = (byte) (96 + ref);
            } else {
                this._buffer[this._offset++] = 79;
                this.writeInt(ref);
            }

            return ref;
        } else {
            ref = this._classRefs.size();
            this._classRefs.put(type, ref);
            if (4096 < this._offset + 32) {
                this.flush();
            }

            this._buffer[this._offset++] = 67;
            this.writeString(type);
            return -1;
        }
    }

    public void writeClassFieldLength(int len) throws IOException {
        this.writeInt(len);
    }

    public void writeObjectEnd() throws IOException {
    }

    private void writeType(String type) throws IOException {
        this.flushIfFull();
        int len = type.length();
        if (len == 0) {
            throw new IllegalArgumentException("empty type is not allowed");
        } else {
            if (this._typeRefs == null) {
                this._typeRefs = new HashMap();
            }

            Integer typeRefV = (Integer) this._typeRefs.get(type);
            if (typeRefV != null) {
                int typeRef = typeRefV;
                this.writeInt(typeRef);
            } else {
                this._typeRefs.put(type, this._typeRefs.size());
                this.writeString(type);
            }

        }
    }

    public void writeBoolean(boolean value) throws IOException {
        if (4096 < this._offset + 16) {
            this.flush();
        }

        if (value) {
            this._buffer[this._offset++] = 84;
        } else {
            this._buffer[this._offset++] = 70;
        }

    }

    public void writeInt(int value) throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;
        if (4096 <= offset + 16) {
            this.flush();
            offset = this._offset;
        }

        if (-16 <= value && value <= 47) {
            buffer[offset++] = (byte) (value + 144);
        } else if (-2048 <= value && value <= 2047) {
            buffer[offset++] = (byte) (200 + (value >> 8));
            buffer[offset++] = (byte) value;
        } else if (-262144 <= value && value <= 262143) {
            buffer[offset++] = (byte) (212 + (value >> 16));
            buffer[offset++] = (byte) (value >> 8);
            buffer[offset++] = (byte) value;
        } else {
            buffer[offset++] = 73;
            buffer[offset++] = (byte) (value >> 24);
            buffer[offset++] = (byte) (value >> 16);
            buffer[offset++] = (byte) (value >> 8);
            buffer[offset++] = (byte) value;
        }

        this._offset = offset;
    }

    public void writeLong(long value) throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;
        if (4096 <= offset + 16) {
            this.flush();
            offset = this._offset;
        }

        if (-8L <= value && value <= 15L) {
            buffer[offset++] = (byte) ((int) (value + 224L));
        } else if (-2048L <= value && value <= 2047L) {
            buffer[offset++] = (byte) ((int) (248L + (value >> 8)));
            buffer[offset++] = (byte) ((int) value);
        } else if (-262144L <= value && value <= 262143L) {
            buffer[offset++] = (byte) ((int) (60L + (value >> 16)));
            buffer[offset++] = (byte) ((int) (value >> 8));
            buffer[offset++] = (byte) ((int) value);
        } else if (-2147483648L <= value && value <= 2147483647L) {
            buffer[offset + 0] = 89;
            buffer[offset + 1] = (byte) ((int) (value >> 24));
            buffer[offset + 2] = (byte) ((int) (value >> 16));
            buffer[offset + 3] = (byte) ((int) (value >> 8));
            buffer[offset + 4] = (byte) ((int) value);
            offset += 5;
        } else {
            buffer[offset + 0] = 76;
            buffer[offset + 1] = (byte) ((int) (value >> 56));
            buffer[offset + 2] = (byte) ((int) (value >> 48));
            buffer[offset + 3] = (byte) ((int) (value >> 40));
            buffer[offset + 4] = (byte) ((int) (value >> 32));
            buffer[offset + 5] = (byte) ((int) (value >> 24));
            buffer[offset + 6] = (byte) ((int) (value >> 16));
            buffer[offset + 7] = (byte) ((int) (value >> 8));
            buffer[offset + 8] = (byte) ((int) value);
            offset += 9;
        }

        this._offset = offset;
    }

    public void writeDouble(double value) throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;
        if (4096 <= offset + 16) {
            this.flush();
            offset = this._offset;
        }

        int intValue = (int) value;
        if ((double) intValue == value) {
            if (intValue == 0) {
                buffer[offset++] = 91;
                this._offset = offset;
                return;
            }

            if (intValue == 1) {
                buffer[offset++] = 92;
                this._offset = offset;
                return;
            }

            if (-128 <= intValue && intValue < 128) {
                buffer[offset++] = 93;
                buffer[offset++] = (byte) intValue;
                this._offset = offset;
                return;
            }

            if (-32768 <= intValue && intValue < 32768) {
                buffer[offset + 0] = 94;
                buffer[offset + 1] = (byte) (intValue >> 8);
                buffer[offset + 2] = (byte) intValue;
                this._offset = offset + 3;
                return;
            }
        }

        int mills = (int) (value * 1000.0D);
        if (0.001D * (double) mills == value) {
            buffer[offset + 0] = 95;
            buffer[offset + 1] = (byte) (mills >> 24);
            buffer[offset + 2] = (byte) (mills >> 16);
            buffer[offset + 3] = (byte) (mills >> 8);
            buffer[offset + 4] = (byte) mills;
            this._offset = offset + 5;
        } else {
            long bits = Double.doubleToLongBits(value);
            buffer[offset + 0] = 68;
            buffer[offset + 1] = (byte) ((int) (bits >> 56));
            buffer[offset + 2] = (byte) ((int) (bits >> 48));
            buffer[offset + 3] = (byte) ((int) (bits >> 40));
            buffer[offset + 4] = (byte) ((int) (bits >> 32));
            buffer[offset + 5] = (byte) ((int) (bits >> 24));
            buffer[offset + 6] = (byte) ((int) (bits >> 16));
            buffer[offset + 7] = (byte) ((int) (bits >> 8));
            buffer[offset + 8] = (byte) ((int) bits);
            this._offset = offset + 9;
        }
    }

    public void writeUTCDate(long time) throws IOException {
        if (4096 < this._offset + 32) {
            this.flush();
        }

        int offset = this._offset;
        byte[] buffer = this._buffer;
        if (time % 60000L == 0L) {
            long minutes = time / 60000L;
            if (minutes >> 31 == 0L || minutes >> 31 == -1L) {
                buffer[offset++] = 75;
                buffer[offset++] = (byte) ((int) (minutes >> 24));
                buffer[offset++] = (byte) ((int) (minutes >> 16));
                buffer[offset++] = (byte) ((int) (minutes >> 8));
                buffer[offset++] = (byte) ((int) (minutes >> 0));
                this._offset = offset;
                return;
            }
        }

        buffer[offset++] = 74;
        buffer[offset++] = (byte) ((int) (time >> 56));
        buffer[offset++] = (byte) ((int) (time >> 48));
        buffer[offset++] = (byte) ((int) (time >> 40));
        buffer[offset++] = (byte) ((int) (time >> 32));
        buffer[offset++] = (byte) ((int) (time >> 24));
        buffer[offset++] = (byte) ((int) (time >> 16));
        buffer[offset++] = (byte) ((int) (time >> 8));
        buffer[offset++] = (byte) ((int) time);
        this._offset = offset;
    }

    public void writeNull() throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;
        if (4096 <= offset + 16) {
            this.flush();
            offset = this._offset;
        }

        buffer[offset++] = 78;
        this._offset = offset;
    }

    public void writeString(String value) throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;
        if (4096 <= offset + 16) {
            this.flush();
            offset = this._offset;
        }

        if (value == null) {
            buffer[offset++] = 78;
            this._offset = offset;
        } else {
            int length = value.length();

            int strOffset;
            int sublen;
            for (strOffset = 0; length > 32768; strOffset += sublen) {
                sublen = 32768;
                offset = this._offset;
                if (4096 <= offset + 16) {
                    this.flush();
                    offset = this._offset;
                }

                char tail = value.charAt(strOffset + sublen - 1);
                if ('\ud800' <= tail && tail <= '\udbff') {
                    --sublen;
                }

                buffer[offset + 0] = 82;
                buffer[offset + 1] = (byte) (sublen >> 8);
                buffer[offset + 2] = (byte) sublen;
                this._offset = offset + 3;
                this.printString(value, strOffset, sublen);
                length -= sublen;
            }

            offset = this._offset;
            if (4096 <= offset + 16) {
                this.flush();
                offset = this._offset;
            }

            if (length <= 31) {
                if (value.startsWith("2.")) {
                    buffer[offset++] = 67;
                } else {
                    buffer[offset++] = (byte) (0 + length);
                }
            } else if (length <= 1023) {
                buffer[offset++] = (byte) (48 + (length >> 8));
                buffer[offset++] = (byte) length;
            } else {
                buffer[offset++] = 83;
                buffer[offset++] = (byte) (length >> 8);
                buffer[offset++] = (byte) length;
            }
//            this._offset=offset;
            if (!value.startsWith("2.")) {
                this._offset = offset;
                this.printString(value, strOffset, length);
            }
        }
    }

    public void writeString(char[] buffer, int offset, int length) throws IOException {
        if (buffer == null) {
            if (4096 < this._offset + 16) {
                this.flush();
            }

            this._buffer[this._offset++] = 78;
        } else {
            while (true) {
                if (length <= 32768) {
                    if (4096 < this._offset + 16) {
                        this.flush();
                    }

                    if (length <= 31) {
                        this._buffer[this._offset++] = (byte) (0 + length);
                    } else if (length <= 1023) {
                        this._buffer[this._offset++] = (byte) (48 + (length >> 8));
                        this._buffer[this._offset++] = (byte) length;
                    } else {
                        this._buffer[this._offset++] = 83;
                        this._buffer[this._offset++] = (byte) (length >> 8);
                        this._buffer[this._offset++] = (byte) length;
                    }

                    this.printString(buffer, offset, length);
                    break;
                }

                int sublen = 32768;
                if (4096 < this._offset + 16) {
                    this.flush();
                }

                char tail = buffer[offset + sublen - 1];
                if ('\ud800' <= tail && tail <= '\udbff') {
                    --sublen;
                }

                this._buffer[this._offset++] = 82;
                this._buffer[this._offset++] = (byte) (sublen >> 8);
                this._buffer[this._offset++] = (byte) sublen;
                this.printString(buffer, offset, sublen);
                length -= sublen;
                offset += sublen;
            }
        }

    }

    public void writeBytes(byte[] buffer) throws IOException {
        if (buffer == null) {
            if (4096 < this._offset + 16) {
                this.flush();
            }

            this._buffer[this._offset++] = 78;
        } else {
            this.writeBytes(buffer, 0, buffer.length);
        }

    }

    public void writeBytes(byte[] buffer, int offset, int length) throws IOException {
        if (buffer == null) {
            if (4096 < this._offset + 16) {
                this.flushBuffer();
            }

            this._buffer[this._offset++] = 78;
        } else {
            this.flush();

            while (4096 - this._offset - 3 < length) {
                int sublen = 4096 - this._offset - 3;
                if (sublen < 16) {
                    this.flushBuffer();
                    sublen = 4096 - this._offset - 3;
                    if (length < sublen) {
                        sublen = length;
                    }
                }

                this._buffer[this._offset++] = 65;
                this._buffer[this._offset++] = (byte) (sublen >> 8);
                this._buffer[this._offset++] = (byte) sublen;
                System.arraycopy(buffer, offset, this._buffer, this._offset, sublen);
                this._offset += sublen;
                length -= sublen;
                offset += sublen;
                this.flushBuffer();
            }

            if (4096 < this._offset + 16) {
                this.flushBuffer();
            }

            if (length <= 15) {
                this._buffer[this._offset++] = (byte) (32 + length);
            } else if (length <= 1023) {
                this._buffer[this._offset++] = (byte) (52 + (length >> 8));
                this._buffer[this._offset++] = (byte) length;
            } else {
                this._buffer[this._offset++] = 66;
                this._buffer[this._offset++] = (byte) (length >> 8);
                this._buffer[this._offset++] = (byte) length;
            }

            System.arraycopy(buffer, offset, this._buffer, this._offset, length);
            this._offset += length;
        }

    }

    public void writeByteBufferStart() throws IOException {
    }

    public void writeByteBufferPart(byte[] buffer, int offset, int length) throws IOException {
        while (length > 0) {
            int sublen = length;
            if (32768 < length) {
                sublen = 32768;
            }

            this.flush();
            this._os.write(65);
            this._os.write(sublen >> 8);
            this._os.write(sublen);
            this._os.write(buffer, offset, sublen);
            length -= sublen;
            offset += sublen;
        }

    }

    public void writeByteBufferEnd(byte[] buffer, int offset, int length) throws IOException {
        this.writeBytes(buffer, offset, length);
    }

    public OutputStream getBytesOutputStream() throws IOException {
        return new Hessian2Output.BytesOutputStream();
    }

    protected void writeRef(int value) throws IOException {
        if (4096 < this._offset + 16) {
            this.flush();
        }

        this._buffer[this._offset++] = 81;
        this.writeInt(value);
    }

    public boolean addRef(Object object) throws IOException {
        int ref = this._refs.get(object);
        if (ref >= 0) {
            this.writeRef(ref);
            return true;
        } else {
            this._refs.put(object, this._refs.size());
            return false;
        }
    }

    public boolean removeRef(Object obj) throws IOException {
        if (this._refs != null) {
            this._refs.remove(obj);
            return true;
        } else {
            return false;
        }
    }

    public boolean replaceRef(Object oldRef, Object newRef) throws IOException {
        Integer value = this._refs.remove(oldRef);
        if (value != null) {
            this._refs.put(newRef, value);
            return true;
        } else {
            return false;
        }
    }

    public void resetReferences() {
        if (this._refs != null) {
            this._refs.clear();
        }

    }

    public void writeStreamingObject(Object obj) throws IOException {
        this.startStreamingPacket();
        this.writeObject(obj);
        this.endStreamingPacket();
    }

    public void startStreamingPacket() throws IOException {
        if (this._refs != null) {
            this._refs.clear();
        }

        this.flush();
        this._isStreaming = true;
        this._offset = 3;
    }

    public void endStreamingPacket() throws IOException {
        int len = this._offset - 3;
        this._buffer[0] = 80;
        this._buffer[1] = (byte) (len >> 8);
        this._buffer[2] = (byte) len;
        this._isStreaming = false;
        this.flush();
    }

    public void printLenString(String v) throws IOException {
        if (4096 < this._offset + 16) {
            this.flush();
        }

        if (v == null) {
            this._buffer[this._offset++] = 0;
            this._buffer[this._offset++] = 0;
        } else {
            int len = v.length();
            this._buffer[this._offset++] = (byte) (len >> 8);
            this._buffer[this._offset++] = (byte) len;
            this.printString((String) v, 0, len);
        }

    }

    public void printString(String v) throws IOException {
        this.printString((String) v, 0, v.length());
    }

    public void printString(String v, int strOffset, int length) throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;

        for (int i = 0; i < length; ++i) {
            if (4096 <= offset + 16) {
                this._offset = offset;
                this.flush();
                offset = this._offset;
            }

            char ch = v.charAt(i + strOffset);
            if (ch < 128) {
                buffer[offset++] = (byte) ch;
            } else if (ch < 2048) {
                buffer[offset++] = (byte) (192 + (ch >> 6 & 31));
                buffer[offset++] = (byte) (128 + (ch & 63));
            } else {
                buffer[offset++] = (byte) (224 + (ch >> 12 & 15));
                buffer[offset++] = (byte) (128 + (ch >> 6 & 63));
                buffer[offset++] = (byte) (128 + (ch & 63));
            }
        }

        this._offset = offset;
    }

    public void printString(char[] v, int strOffset, int length) throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;

        for (int i = 0; i < length; ++i) {
            if (4096 <= offset + 16) {
                this._offset = offset;
                this.flush();
                offset = this._offset;
            }

            char ch = v[i + strOffset];
            if (ch < 128) {
                buffer[offset++] = (byte) ch;
            } else if (ch < 2048) {
                buffer[offset++] = (byte) (192 + (ch >> 6 & 31));
                buffer[offset++] = (byte) (128 + (ch & 63));
            } else {
                buffer[offset++] = (byte) (224 + (ch >> 12 & 15));
                buffer[offset++] = (byte) (128 + (ch >> 6 & 63));
                buffer[offset++] = (byte) (128 + (ch & 63));
            }
        }

        this._offset = offset;
    }

    private final void flushIfFull() throws IOException {
        int offset = this._offset;
        if (4096 < offset + 32) {
            this._offset = 0;
            this._os.write(this._buffer, 0, offset);
        }

    }

    public final void flush() throws IOException {
        this.flushBuffer();
        if (this._os != null) {
            this._os.flush();
        }

    }

    public final void flushBuffer() throws IOException {
        int offset = this._offset;
        if (!this._isStreaming && offset > 0) {
            this._offset = 0;
            this._os.write(this._buffer, 0, offset);
        } else if (this._isStreaming && offset > 3) {
            int len = offset - 3;
            this._buffer[0] = 112;
            this._buffer[1] = (byte) (len >> 8);
            this._buffer[2] = (byte) len;
            this._offset = 3;
            this._os.write(this._buffer, 0, offset);
        }

    }

    public final void close() throws IOException {
        this.flush();
        OutputStream os = this._os;
        this._os = null;
        if (os != null && this._isCloseStreamOnClose) {
            os.close();
        }

    }

    public void setSerializerFactory(SerializerFactory serializerFactory) {
    }

    class BytesOutputStream extends OutputStream {
        private int _startOffset;

        BytesOutputStream() throws IOException {
            if (4096 < Hessian2Output.this._offset + 16) {
                Hessian2Output.this.flush();
            }

            this._startOffset = Hessian2Output.this._offset;
            Hessian2Output.this._offset = Hessian2Output.this._offset + 3;
        }

        public void write(int ch) throws IOException {
            if (4096 <= Hessian2Output.this._offset) {
                int length = Hessian2Output.this._offset - this._startOffset - 3;
                Hessian2Output.this._buffer[this._startOffset] = 65;
                Hessian2Output.this._buffer[this._startOffset + 1] = (byte) (length >> 8);
                Hessian2Output.this._buffer[this._startOffset + 2] = (byte) length;
                Hessian2Output.this.flush();
                this._startOffset = Hessian2Output.this._offset;
                Hessian2Output.this._offset = Hessian2Output.this._offset + 3;
            }

            Hessian2Output.this._buffer[Hessian2Output.this._offset++] = (byte) ch;
        }

        public void write(byte[] buffer, int offset, int length) throws IOException {
            while (length > 0) {
                int sublen = 4096 - Hessian2Output.this._offset;
                if (length < sublen) {
                    sublen = length;
                }

                if (sublen > 0) {
                    System.arraycopy(buffer, offset, Hessian2Output.this._buffer, Hessian2Output.this._offset, sublen);
                    Hessian2Output.this._offset = Hessian2Output.this._offset + sublen;
                }

                length -= sublen;
                offset += sublen;
                if (4096 <= Hessian2Output.this._offset) {
                    int chunkLength = Hessian2Output.this._offset - this._startOffset - 3;
                    Hessian2Output.this._buffer[this._startOffset] = 65;
                    Hessian2Output.this._buffer[this._startOffset + 1] = (byte) (chunkLength >> 8);
                    Hessian2Output.this._buffer[this._startOffset + 2] = (byte) chunkLength;
                    Hessian2Output.this.flush();
                    this._startOffset = Hessian2Output.this._offset;
                    Hessian2Output.this._offset = Hessian2Output.this._offset + 3;
                }
            }

        }

        public void close() throws IOException {
            int startOffset = this._startOffset;
            this._startOffset = -1;
            if (startOffset >= 0) {
                int length = Hessian2Output.this._offset - startOffset - 3;
                Hessian2Output.this._buffer[startOffset] = 66;
                Hessian2Output.this._buffer[startOffset + 1] = (byte) (length >> 8);
                Hessian2Output.this._buffer[startOffset + 2] = (byte) length;
                Hessian2Output.this.flush();
            }
        }
    }
}

