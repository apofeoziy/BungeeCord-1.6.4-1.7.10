package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.netty.Var;

public class PluginMessageRewriter extends PacketRewriter
{

    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        String channel = Var.readString(in, true);
        Var.writeString( channel, out, false );
        short length = in.readShort();

        if ( "MC|AdvCmd".equals( channel ) ) {
            ByteBuf buf = Unpooled.buffer();
            byte mode = in.readByte();
            buf.writeByte( mode );
            if ( mode == 0x0 )
            {
                buf.writeBytes( in.readBytes( 12 ) );
                Var.writeString( Var.readString( in, true ), buf, false );
            } else if ( mode == 0x1 )
            {
                buf.writeBytes( in.readBytes( 4 ) );
                Var.writeString( Var.readString( in, true ), buf, false );
            } else
            {
                buf.writeBytes( in.readBytes( length - 1 ) );
            }
            out.writeShort( buf.readableBytes() );
            out.writeBytes( in.readBytes( in.readableBytes() ) );
        } else {
            out.writeShort(length);
            out.writeBytes( in.readBytes( length ) );
        }
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        String channel = Var.readString( in, false );
        Var.writeString( channel, out, true );
        out.writeBytes( in.readBytes( in.readableBytes() ) );
    }

}
