package net.md_5.bungee.netty;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class Var {
    public static void writeString(String s, ByteBuf buf, boolean varint)
    {
        if ( varint )
        {
            byte[] content = s.getBytes( Charset.forName( "UTF-8" ) );
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
            int len = readVarInt( buf );
            byte[] content = new byte[ len ];
            buf.readBytes( content );
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

    public static void writeArray(byte[] b, ByteBuf buf)
    {
        // TODO: Check len - use Guava?
        buf.writeShort( b.length );
        buf.writeBytes( b );
    }

    public static byte[] readArray(ByteBuf buf)
    {
        // TODO: Check len - use Guava?
        short len = buf.readShort();
        byte[] ret = new byte[ len ];
        buf.readBytes( ret );
        return ret;
    }

    public static int readVarInt(ByteBuf input)
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

    public static void writeVarInt(int value, ByteBuf output)
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

    public static void rewriteEntityMetadata(ByteBuf in, ByteBuf out) {
        while ( true ) {
            int item = in.readUnsignedByte();
            out.writeByte( item );
            if ( item == 0x7F ) {
                break;
            }
            int type = item >> 5;
            if ( type == 0 ) out.writeByte( in.readByte() );
            else if ( type == 1 ) out.writeShort( in.readShort() );
            else if ( type == 2 ) out.writeInt( in.readInt() );
            else if ( type == 3 ) out.writeFloat( in.readFloat() );
            else if ( type == 4 ) Var.writeString( Var.readString( in, false ), out, true );
            else if ( type == 5 ) {
                rewriteItemData( in, out );
            } else if ( type == 6 ) {
                out.writeInt( in.readInt() );
                out.writeInt( in.readInt() );
                out.writeInt( in.readInt() );
            }
        }
    }

    public static void rewriteItemData(ByteBuf in, ByteBuf out)
    {
        short streamId = in.readShort();
        short itemType = streamId;

        if ( streamId != -1 && PacketMapping.itemIdMapping[ streamId ] != 0 )
        {
            itemType = PacketMapping.itemIdMapping[ streamId ];
        }

        out.writeShort( itemType );
        if ( streamId >= 0 )
        {
            out.writeBytes( in.readBytes( 3 ) );
            short bytes = in.readShort();
            out.writeShort( bytes );
            if ( bytes >= 0 )
            {
                out.writeBytes( in.readBytes( bytes ) );
            }
        }
    }
}

