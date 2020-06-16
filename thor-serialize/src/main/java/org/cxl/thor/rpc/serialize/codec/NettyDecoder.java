package org.cxl.thor.rpc.serialize.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.cxl.thor.rpc.serialize.Serializer;

import java.util.List;

/**
 * @author cxl
 * @Description:netty Netty解码器
 * @date 2020/6/10 12:05
 */
public class NettyDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    private Serializer serializer;

    public NettyDecoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object obj = serializer.deserialize(data);
        out.add(obj);
    }


}
