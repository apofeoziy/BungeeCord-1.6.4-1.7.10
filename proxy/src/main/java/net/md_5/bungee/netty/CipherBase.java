package net.md_5.bungee.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

/**
 * Class to expose an
 * {@link #cipher(io.netty.buffer.ByteBuf, io.netty.buffer.ByteBuf)} method to
 * aid in the efficient passing of ByteBuffers through a cipher.
 */
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class CipherBase
{

    @NonNull
    private final Cipher cipher;
    private ThreadLocal<byte[]> heapInLocal = new EmptyByteThreadLocal();
    private ThreadLocal<byte[]> heapOutLocal = new EmptyByteThreadLocal();

    private static class EmptyByteThreadLocal extends ThreadLocal<byte[]>
    {

        @Override
        protected byte[] initialValue()
        {
            return new byte[ 0 ];
        }
    }

    private byte[] bufToByte(ByteBuf in)
    {
        byte[] heapIn = heapInLocal.get();
        int readableBytes = in.readableBytes();
        if ( heapIn.length < readableBytes )
        {
            heapIn = new byte[ readableBytes ];
            heapInLocal.set( heapIn );
        }
        in.readBytes( heapIn, 0, readableBytes );
        return heapIn;
    }

    public ByteBuf cipher(ChannelHandlerContext ctx, ByteBuf in) throws ShortBufferException
    {
        int readableBytes = in.readableBytes();
        byte[] heapIn = bufToByte( in );

        ByteBuf heapOut = ctx.alloc().heapBuffer( cipher.getOutputSize( readableBytes ) );
        heapOut.writerIndex( cipher.update( heapIn, 0, readableBytes, heapOut.array(), heapOut.arrayOffset() ) );

        return heapOut;
    }

    public void cipher(ByteBuf in, ByteBuf out) throws ShortBufferException
    {
        int readableBytes = in.readableBytes();
        byte[] heapIn = bufToByte( in );

        byte[] heapOut = heapOutLocal.get();
        int outputSize = cipher.getOutputSize( readableBytes );
        if ( heapOut.length < outputSize )
        {
            heapOut = new byte[ outputSize ];
            heapOutLocal.set( heapOut );
        }
        out.writeBytes( heapOut, 0, cipher.update( heapIn, 0, readableBytes, heapOut ) );
    }
}
