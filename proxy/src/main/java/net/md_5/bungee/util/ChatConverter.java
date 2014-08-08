package net.md_5.bungee.util;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.protocol.packet.Packet3Chat;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatConverter
{
    //NOTE: Inspired by craftspigot: org.spigotmc.ChatConverter
    public static final GsonBuilder GSON_BUILDER = new GsonBuilder().registerTypeAdapter( Message.class, new MessageSerializer() );
    private static final char COLOR_CHAR = '\u00A7';
    private static final Pattern URL_PATTERN = Pattern.compile( "^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$" );

    public static Packet3Chat[] fixJSONChat(final JsonObject inputObject)
    {
        if ( inputObject.has( "using" ) )
        {
            JsonElement element = inputObject.remove( "using" );
            inputObject.add( "with", element );
        }

        String[] textToTranslate;
        if ( inputObject.has( "text" ) && inputObject.entrySet().size() == 1 )
        {
            textToTranslate = inputObject.get( "text" ).getAsString().split( "\n" );
        } else if ( inputObject.isJsonPrimitive() )
        {
            textToTranslate = inputObject.getAsString().split( "\n" );
        } else
        {
            return new Packet3Chat[] { new Packet3Chat( GSON_BUILDER.create().toJson( inputObject ) ) };
        }

        Packet3Chat[] result = new Packet3Chat[ textToTranslate.length ];
        for ( int i = 0; i < result.length; i++ )
        {
            result[ i ] = new Packet3Chat( GSON_BUILDER.create().toJson( jsonifyMessage( textToTranslate[ i ] ) ) );
        }
        return result;
    }

    private static List<Message> jsonifyMessage(final String inputText)
    {
        Message msg = new Message();
        List<Message> parts = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        Matcher matcher = URL_PATTERN.matcher( inputText );
        for ( int i = 0; i < inputText.length(); i++ )
        {
            char c = inputText.charAt( i );
            if ( c != COLOR_CHAR )
            {
                int pos = inputText.indexOf( ' ', i );
                if ( pos == -1 )
                {
                    pos = inputText.length();
                }
                if ( matcher.region( i, pos ).find() )
                { //Web link handling
                    msg.text = buf.toString();
                    buf = new StringBuilder();
                    parts.add( msg );
                    Message old = msg;
                    msg = new Message( old );
                    msg.clickEvent = new ClickEvent();
                    msg.clickEvent.action = "open_url";
                    String urlString = inputText.substring( i, pos );
                    if ( urlString.startsWith( "http" ) )
                    {
                        msg.text = msg.clickEvent.value = urlString;
                    } else
                    {
                        msg.text = urlString;
                        msg.clickEvent.value = "http://" + urlString;
                    }
                    parts.add( msg );
                    i += pos - i - 1;
                    msg = new Message( old );
                    continue;
                }
                buf.append( c );
                continue;
            }
            i++;
            c = inputText.charAt( i );
            if ( c >= 'A' && c <= 'Z' )
            {
                c += 32;
            }
            msg.text = buf.toString();
            buf = new StringBuilder();
            parts.add( msg );
            msg = new Message( msg );
            switch ( c )
            {
                case 'k':
                    msg.obfuscated = Boolean.TRUE;
                    break;
                case 'l':
                    msg.bold = Boolean.TRUE;
                    break;
                case 'm':
                    msg.strikethrough = Boolean.TRUE;
                    break;
                case 'n':
                    msg.underlined = Boolean.TRUE;
                    break;
                case 'o':
                    msg.italic = Boolean.TRUE;
                    break;
                default:
                    msg.obfuscated = false;
                    msg.bold = false;
                    msg.strikethrough = false;
                    msg.underlined = false;
                    msg.italic = false;
                    if ( c != 'r' )
                    {
                        msg.color = Color.fromCode( Character.toString( c ) );
                    } else
                    {
                        msg.color = Color.WHITE;
                    }
                    break;
            }
        }
        msg.text = buf.toString();
        parts.add( msg );
        return parts;
    }
}

@Data
@NoArgsConstructor
class Message
{
    public String text;

    public Boolean bold;
    public Boolean italic;
    public Boolean underlined;
    public Boolean strikethrough;
    public Boolean obfuscated;

    public Color color;

    public ClickEvent clickEvent;

    public Message(Message old)
    {
        this.bold = old.bold;
        this.italic = old.italic;
        this.underlined = old.underlined;
        this.strikethrough = old.strikethrough;
        this.color = old.color;
    }
}

class MessageSerializer implements JsonSerializer<Message>
{

    @Override
    public JsonElement serialize(Message src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject object = new JsonObject();

        for ( Field field : Message.class.getDeclaredFields() )
        {
            try
            {
                if ( field.getType().equals( boolean.class ) && !field.getBoolean( src ) ) //Don't write formating stuff - Defaults to false -> bandwidth :)
                {
                    continue;
                }

                object.add( field.getName(), context.serialize( field.get( src ) ) );
            } catch ( IllegalArgumentException | IllegalAccessException ex )
            {
                throw new AssertionError( ex );
            }
        }

        return object;
    }

}

class ClickEvent
{

    public String action;
    public String value;
}

@RequiredArgsConstructor
enum Color
{

    @SerializedName("black")
    BLACK( "0" ),
    @SerializedName("dark_blue")
    DARK_BLUE( "1" ),
    @SerializedName("dark_green")
    DARK_GREEN( "2" ),
    @SerializedName("dark_aqua")
    DARK_AQUA( "3" ),
    @SerializedName("dark_red")
    DARK_RED( "4" ),
    @SerializedName("dark_purple")
    DARK_PURPLE( "5" ),
    @SerializedName("gold")
    GOLD( "6" ),
    @SerializedName("gray")
    GRAY( "7" ),
    @SerializedName("dark_gray")
    DARK_GRAY( "8" ),
    @SerializedName("blue")
    BLUE( "9" ),
    @SerializedName("green")
    GREEN( "a" ),
    @SerializedName("aqua")
    AQUA( "b" ),
    @SerializedName("red")
    RED( "c" ),
    @SerializedName("light_purple")
    LIGHT_PURPLE( "d" ),
    @SerializedName("yellow")
    YELLOW( "e" ),
    @SerializedName("white")
    WHITE( "f" );

    public String code;

    Color(String code)
    {
        this.code = code;
    }

    private static final HashMap<String, Color> codeMap = new HashMap<>();

    public static Color fromCode(String code)
    {
        return codeMap.get( code );
    }

    static
    {
        for ( Color color : values() )
        {
            codeMap.put( color.code, color );
        }
    }
}