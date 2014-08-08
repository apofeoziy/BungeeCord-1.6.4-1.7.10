package net.md_5.bungee.protocol.packet.protocolhack;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.packet.DefinedPacket;

import java.nio.charset.Charset;

public abstract class Defined172Packet extends DefinedPacket {

    Defined172Packet(int packetId) {
        super( packetId );
    }

    static int readVarInt(ByteBuf input)
    {
        int out = 0;
        int bytes = 0;
        byte in;
        while ( true )
        {
            in = input.readByte();

            out |= ( in & 0x7F ) << ( bytes++ * 7 );

            if ( bytes > 32 )
            {
                throw new RuntimeException( "VarInt too big" );
            }

            if ( ( in & 0x80 ) != 0x80 )
            {
                break;
            }
        }
        return out;
    }

    public void writeVarInt(int value, ByteBuf output)
    {
        int part;
        while ( true )
        {
            part = value & 0x7F;

            value >>>= 7;
            if ( value != 0 )
            {
                part |= 0x80;
            }

            output.writeByte( part );

            if ( value == 0 )
            {
                break;
            }
        }
    }

    public void writeString(String s, ByteBuf buf, boolean varint)
    {
        if ( varint )
        {
            byte[] content = s.getBytes( Charset.forName("UTF-8") );
            writeVarInt( content.length, buf );
            buf.writeBytes( content );
        }
        else {
            buf.writeShort( s.length() );
            for ( char c : s.toCharArray() )
            {
                buf.writeChar( c );
            }
        }
    }

    public static String readString(ByteBuf buf, boolean varint)
    {
        if ( varint )
        {
            int len = readVarInt(buf);
            byte[] content = new byte[ len ];
            buf.readBytes(content);
            return new String( content, Charset.forName( "UTF-8" ) );
        }
        else {
            short len = buf.readShort();
            char[] chars = new char[ len ];
            for ( int i = 0; i < len; i++ )
            {
                chars[i] = buf.readChar();
            }
            return new String( chars );
        }
    }
}
