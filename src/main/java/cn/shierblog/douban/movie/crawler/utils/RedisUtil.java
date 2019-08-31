package cn.shierblog.douban.movie.crawler.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedisUtil
 * @Description Redis 工具类
 * @Author zyb
 * @Date 2018/11/4  18:42
 * @Version 1.0
 *
 *
 * 一、keys相关命令
 *
 * 序号	方法	描述
 * 1	void delete(String key)	key 存在时删除 key
 * 2	void delete(Collection keys)	批量删除key
 * 3	byte[] dump(String key)	序列化给定 key ，并返回被序列化的值
 * 4	Boolean hasKey(String key)	检查给定 key 是否存在
 * 5	Boolean expire(String key, long timeout, TimeUnit unit)	设置过期时间
 * 6	Boolean expireAt(String key, Date date)	设置过期时间
 * 7	Set keys(String pattern)	查找所有符合给定模式( pattern)的 key
 * 8	Boolean move(String key, int dbIndex)	将当前数据库的 key 移动到给定的数据库 db 当中
 * 9	Boolean persist(String key)	移除 key 的过期时间，key 将持久保持
 * 10	Long getExpire(String key, TimeUnit unit)	返回 key 的剩余的过期时间
 * 11	Long getExpire(String key)	返回 key 的剩余的过期时间
 * 12	String randomKey()	从当前数据库中随机返回一个 key
 * 13	void rename(String oldKey, String newKey)	修改 key 的名称
 * 14	Boolean renameIfAbsent(String oldKey, String newKey)	仅当 newkey 不存在时，将 oldKey 改名为 newkey
 * 15	DataType type(String key)	返回 key 所储存的值的类型
 * 二、String数据类型操作
 *
 * 序号	方法	描述
 * 1	String get(String key)	获取指定 key 的值
 * 2	String getRange(String key, long start, long end)	返回 key 中字符串值的子字符
 * 3	String getAndSet(String key, String value)	将给定 key 的值设为 value ，并返回key
 * 的旧值(old value)
 * 4	Boolean getBit(String key, long offset)	对 key 所储存的字符串值，获取指定偏移
 * 量上的位(bit)
 * 5	List multiGet(Collection keys)	批量获取
 * 6	void set(String key, String value)	设置指定 key 的值
 * 7	boolean setBit(String key, long offset, boolean value)	设置ASCII码, 字符串'a'的ASCII码是97, 转
 * 为二进制是'01100001', 此方法是将
 * 二进制第offset位值变为value
 * 8	void setEx(String key, String value, long timeout, TimeUnit unit)	将值 value 关联到 key ，并将 key 的过期
 * 时间设为 timeout,unit:时间单位,
 * 天:TimeUnit.DAYS 小时:TimeUnit.HOURS
 * 分钟:TimeUnit.MINUTES,
 * 秒:TimeUnit.SECONDS
 * 毫秒:TimeUnit.MILLISECONDS
 * 9	boolean setIfAbsent(String key, String value)	只有在 key 不存在时设置 key 的值
 * 10	void setRange(String key, String value, long offset)	用 value 参数覆写给定 key 所储存的字符串
 * 值，从偏移量 offset 开始
 * 11	void multiSet(Map<String,String> maps)	批量添加
 * 12	boolean multiSetIfAbsent(Map<String,String> maps)	同时设置一个或多个 key-value 对，当且仅
 * 当所有给定 key 都不存在
 * 13	Integer append(String key, String value)	追加到末尾
 * 14	Long incrBy(String key, long increment)	增加(自增长), 负数则为自减
 * 15	Double incrByFloat(String key, double increment)	增加(自增长), 负数则为自减
 * 16	Long size(String key)	获取字符串的长度
 * 三、Hash相关的操作
 *
 * 序号	方法	描述
 * 1	Object hGet(String key, String field)	获取存储在哈希表中指定字段的值
 * 2	Map hGetAll(String key)	获取所有给定字段的值
 * 3	List hMultiGet(String key, Collection fields)	获取所有给定字段的值
 * 4	void hPut(String key, String hashKey, String value)	添加字段
 * 5	void hPutAll(String key, Map maps)	添加多个字段
 * 6	Boolean hPutIfAbsent(String key, String hashKey, String value)	仅当hashKey不存在时才设置
 * 7	Long hDelete(String key, Object... fields)	删除一个或多个哈希表字段
 * 8	boolean hExists(String key, String field)	查看哈希表 key 中，指定的字段是
 * 否存在
 * 9	Long hIncrBy(String key, Object field, long increment)	为哈希表 key 中的指定字段的整数
 * 值加上增量 increment
 * 10	Double hIncrByFloat(String key, Object field, double delta)	为哈希表 key 中的指定字段的整数
 * 值加上增量 increment
 * 11	Set hKeys(String key)	获取所有哈希表中的字段
 * 12	Long hSize(String key)	获取哈希表中字段的数量
 * 13	List hValues(String key)	获取哈希表中所有值
 * 14	Cursor hScan(String key, ScanOptions options)	迭代哈希表中的键值对
 * 四、List相关的操作
 *
 * 序号	方法	描述
 * 1	String lIndex(String key, long index)	通过索引获取列表中的元素
 * 2	List lRange(String key, long start, long end)	获取列表指定范围内的元素
 * 3	Long lLeftPush(String key, String value)	存储在list头部
 * 4	Long lLeftPushAll(String key, String... value)	存储在list头部
 * 5	Long lLeftPushAll(String key, Collection value)	存储在list头部
 * 6	Long lLeftPushIfPresent(String key, String value)	当list存在的时候才加入
 * 7	lLeftPush(String key, String pivot, String value)	如果pivot存在,再pivot前面添加
 * 8	Long lRightPush(String key, String value)	存储在list尾部
 * 9	Long lRightPushAll(String key, String... value)	存储在list尾部
 * 10	Long lRightPushAll(String key, Collection value)	存储在list尾部
 * 11	Long lRightPushIfPresent(String key, String value)	当list存在的时候才加入
 * 12	lRightPush(String key, String pivot, String value)	在pivot元素的右边添加值
 * 13	void lSet(String key, long index, String value)	通过索引设置列表元素的值
 * 14	String lLeftPop(String key)	移出并获取列表的第一个元素
 * 15	String lBLeftPop(String key, long timeout, TimeUnit unit)	移出并获取列表的第一个元素， 如果列
 * 表没有元素会阻塞列表直到等待超时或
 * 发现可弹出元素为止
 * 16	String lRightPop(String key)	移除并获取列表最后一个元素
 * 17	String lBRightPop(String key, long timeout, TimeUnit unit)	移出并获取列表的最后一个元素， 如
 * 果列表没有元素会阻塞列表直到等待超时
 * 或发现可弹出元素为止
 * 18	String lRightPopAndLeftPush(String sourceKey, String destinationKey)	移除列表的最后一个元素，
 * 并将该元素添加到另一个列表并返回
 * 19	String lBRightPopAndLeftPush(String sourceKey, String destinationKey,,long timeout, TimeUnit unit)	从列表中弹出一个值，将弹出的元素插入到
 * 另外一个列表中并返回它； 如果列表没
 * 有元素会阻塞列表直到等待超时或发现可弹出
 * 元素为止
 * 20	Long lRemove(String key, long index, String value)	删除集合中值等于value得元素
 * 21	void lTrim(String key, long start, long end)	裁剪list
 * 22	Long lLen(String key)	获取列表长度
 * 五、Set相关的操作
 *
 * 1.添加操作：
 *
 * //添加
 * Long sAdd(String key, String... values);
 *
 * 2.获取操作：
 *
 * //获取集合所有元素
 * Set<String> sMembers(String key);
 *
 * //获取集合大小
 * Long sSize(String key);
 *
 * //判断集合是否包含value
 * Boolean sIsMember(String key, Object value);
 *
 * //随机获取集合中的一个元素
 * String sRandomMember(String key);
 *
 * //随机获取集合count个元素
 * List<String> sRandomMembers(String key, long count);
 *
 * //随机获取集合中count个元素并且去除重复的
 * Set<String> sDistinctRandomMembers(String key, long count);
 *
 * //使用迭代器获取元素
 * Cursor<String> sScan(String key, ScanOptions options);
 *
 * //-------------------------------------------------------------------------------------
 *
 * //获取两个集合的交集
 * Set<String> sIntersect(String key, String otherKey);
 *
 * //获取key集合与多个集合的交集
 * Set<String> sIntersect(String key, Collection<String> otherKeys);
 *
 * //key集合与destKey集合的交集存储到destKey集合中
 * Long sIntersectAndStore(String key, String otherKey, String destKey);
 *
 * //key集合与多个集合的交集存储到destKey集合中
 * Long sIntersectAndStore(String key, Collection<String> otherKeys, String destKey)
 *
 * //--------------------------------------------------------------------------------------
 *
 * //获取两个集合的并集
 * Set<String> sUnion(String key, String otherKeys);
 *
 * //获取key集合与多个集合的并集
 * Set<String> sUnion(String key, Collection<String> otherKeys);
 *
 * //key集合与otherKey集合的并集存储到destKey中
 * Long sUnionAndStore(String key, String otherKey, String destKey);
 *
 * //key集合与多个集合的并集存储到destKey中
 * Long sUnionAndStore(String key, Collection<String> otherKeys, String destKey);
 *
 * //-------------------------------------------------------------------------------------
 *
 * //获取两个集合的差集
 * Set<String> sDifference(String key, String otherKey);
 *
 * //获取key集合与多个集合的差集
 * Set<String> sDifference(String key, Collection<String> otherKeys);
 *
 * //key集合与otherKey集合的差集存储到destKey中
 * Long sDifference(String key, String otherKey, String destKey);
 *
 * //key集合与多个集合的差集存储到destKey中
 * Long sDifference(String key, Collection<String> otherKeys, String destKey);
 *
 *
 * 3.修改操作：
 *
 * //移除
 * Long sRemove(String key, Object... values);
 *
 * //随机移除一个元素
 * String sPop(String key);
 *
 * //将key集合中value元素移到destKey集合中
 * Boolean sMove(String key, String value, String destKey);
 *
 * 六、zset数据类型操作
 *
 * 1.添加操作：
 *
 * //添加元素,有序集合是按照元素的score值由小到大排列
 * Boolean zAdd(String key, String value, double score);
 *
 * //批量添加
 * Long zAdd(String key, Set<TypedTuple<String>> values);
 * //TypedTuple使用
 * TypedTuple<String> objectTypedTuple1 = new DefaultTypedTuple<String>(value, score);
 *
 * 2.获取操作：
 *
 * //获取集合的元素, 从小到大排序, start开始位置, end结束位置
 * Set<String> zRange(String key, long start, long end);
 *
 * //获取集合元素, 并且把score值也获取
 * Set<TypedTuple<String>> zRangeWithScores(String key, long start, long end);
 *
 * //根据Score值查询集合元素的值, 从小到大排序
 * Set<String> zRangeByScore(String key, double min, double max);
 *
 * //根据Score值查询集合元素, 从小到大排序
 * Set<TypedTuple<String>> zRangeByScoreWithScores(String key, double min, double max);
 *
 * //根据Score值查询集合元素, 从小到大排序
 * Set<TypedTuple<String>> zRangeByScoreWithScores(String key, double min, double max, long start, long end);
 *
 * //----------------------------------------------------------------------------------
 *
 * //获取集合的元素, 从大到小排序
 * Set<String> zReverseRange(String key, long start, long end);
 *
 * //获取集合的元素, 从大到小排序, 并返回score值
 * Set<TypedTuple<String>> zReverseRangeWithScores(String key, long start, long end);
 *
 * //根据Score值查询集合元素, 从大到小排序
 * Set<String> zReverseRangeByScore(String key, double min, double max);
 *
 * //根据Score值查询集合元素, 从大到小排序
 * Set<TypedTuple<String>> zReverseRangeByScoreWithScores(String key, double min, double max);
 *
 * //
 * Set<String> zReverseRangeByScore(String key, double min, double max, long start, long end);
 *
 * //-----------------------------------------------------------------------------------
 *
 * //返回元素在集合的排名,有序集合是按照元素的score值由小到大排列
 * Long zRank(String key, Object value);
 *
 * //返回元素在集合的排名,按元素的score值由大到小排列
 * Long zReverseRank(String key, Object value);
 *
 * //根据score值获取集合元素数量
 * Long zCount(String key, double min, double max);
 *
 * //获取集合大小
 * Long zSize(String key);
 *
 * //获取集合大小
 * Long zZCard(String key);
 *
 * //获取集合中value元素的score值
 * Double zScore(String key, Object value);
 *
 * //------------------------------------------------------------------------------------
 *
 * //获取key和otherKey的并集并存储在destKey中
 * Long zUnionAndStore(String key, String otherKey, String destKey);
 *
 * //获取key和多个集合的并集并存储在destKey中
 * Long zUnionAndStore(String key, Collection<String> otherKeys, String destKey)
 *
 * //-----------------------------------------------------------------------------------
 *
 * //获取key和otherKey的交集并存储在destKey中
 * Long zIntersectAndStore(String key, String otherKey, String destKey);
 *
 * //获取key和多个集合的交集并存储在destKey中
 * Long zIntersectAndStore(String key, Collection<String> otherKeys, String destKey);
 *
 * //-----------------------------------------------------------------------------------
 *
 * //使用迭代器获取
 * Cursor<TypedTuple<String>> zScan(String key, ScanOptions options);
 *
 * 3.修改操作：
 *
 * //移除
 * Long zRemove(String key, Object... values);
 *
 * //增加元素的score值，并返回增加后的值
 * Double zIncrementScore(String key, String value, double delta);
 *
 * //移除指定索引位置的成员
 * Long zRemoveRange(String key, long start, long end);
 *
 * //根据指定的score值的范围来移除成员
 * Long zRemoveRangeByScore(String key, double min, double max);
 */


/**
 * spring.redis.host=localhost
 * # 连接超时时间（毫秒）
 * spring.redis.timeout=10000
 * # Redis默认情况下有16个分片，这里配置具体使用的分片，默认是0
 * spring.redis.database=0
 * # 连接池最大连接数（使用负值表示没有限制） 默认 8
 * spring.redis.lettuce.pool.max-active=8
 * # 连接池最大阻塞等待时间（使用负值表示没有限制） 默认 -1
 * spring.redis.lettuce.pool.max-wait=-1
 * # 连接池中的最大空闲连接 默认 8
 * spring.redis.lettuce.pool.max-idle=8
 * # 连接池中的最小空闲连接 默认 0
 * spring.redis.lettuce.pool.min-idle=0
 */
@Component
public class RedisUtil {
    @Autowired
    private StringRedisTemplate redisTemplate;

//    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    public StringRedisTemplate getRedisTemplate() {
//        return this.redisTemplate;
//    }

    /** -------------------key相关操作--------------------- */

    /**
     * 删除key
     *
     * @param key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除key
     *
     * @param keys
     */
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 序列化key
     *
     * @param key
     * @return
     */
    public byte[] dump(String key) {
        return redisTemplate.dump(key);
    }

    /**
     * 是否存在key
     *
     * @param key
     * @return
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param date
     * @return
     */
    public Boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    /**
     * 查找匹配的key
     *
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 将当前数据库的 key 移动到给定的数据库 db 当中
     *
     * @param key
     * @param dbIndex
     * @return
     */
    public Boolean move(String key, int dbIndex) {
        return redisTemplate.move(key, dbIndex);
    }

    /**
     * 移除 key 的过期时间，key 将持久保持
     *
     * @param key
     * @return
     */
    public Boolean persist(String key) {
        return redisTemplate.persist(key);
    }

    /**
     * 返回 key 的剩余的过期时间
     *
     * @param key
     * @param unit
     * @return
     */
    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 返回 key 的剩余的过期时间
     *
     * @param key
     * @return
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 从当前数据库中随机返回一个 key
     *
     * @return
     */
    public String randomKey() {
        return redisTemplate.randomKey();
    }

    /**
     * 修改 key 的名称
     *
     * @param oldKey
     * @param newKey
     */
    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 仅当 newkey 不存在时，将 oldKey 改名为 newkey
     *
     * @param oldKey
     * @param newKey
     * @return
     */
    public Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 返回 key 所储存的值的类型
     *
     * @param key
     * @return
     */
    public DataType type(String key) {
        return redisTemplate.type(key);
    }

    /** -------------------string相关操作--------------------- */

    /**
     * 设置指定 key 的值
     *
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 获取指定 key 的值
     *
     * @param key
     * @return
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 返回 key 中字符串值的子字符
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public String getRange(String key, long start, long end) {
        return redisTemplate.opsForValue().get(key, start, end);
    }

    /**
     * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)
     *
     * @param key
     * @param value
     * @return
     */
    public String getAndSet(String key, String value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 对 key 所储存的字符串值，获取指定偏移量上的位(bit)
     *
     * @param key
     * @param offset
     * @return
     */
    public Boolean getBit(String key, long offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * 批量获取
     *
     * @param keys
     * @return
     */
    public List<String> multiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 设置ASCII码, 字符串'a'的ASCII码是97, 转为二进制是'01100001', 此方法是将二进制第offset位值变为value
     *
     * @param key
     * @param
     * @param value 值,true为1, false为0
     * @return
     */
    public boolean setBit(String key, long offset, boolean value) {
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * 将值 value 关联到 key ，并将 key 的过期时间设为 timeout
     *
     * @param key
     * @param value
     * @param timeout 过期时间
     * @param unit    时间单位, 天:TimeUnit.DAYS 小时:TimeUnit.HOURS 分钟:TimeUnit.MINUTES
     *                秒:TimeUnit.SECONDS 毫秒:TimeUnit.MILLISECONDS
     */
    public void setEx(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 只有在 key 不存在时设置 key 的值
     *
     * @param key
     * @param value
     * @return 之前已经存在返回false, 不存在返回true
     */
    public boolean setIfAbsent(String key, String value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始
     *
     * @param key
     * @param value
     * @param offset 从指定位置开始覆写
     */
    public void setRange(String key, String value, long offset) {
        redisTemplate.opsForValue().set(key, value, offset);
    }

    /**
     * 获取字符串的长度
     *
     * @param key
     * @return
     */
    public Long size(String key) {
        return redisTemplate.opsForValue().size(key);
    }

    /**
     * 批量添加
     *
     * @param maps
     */
    public void multiSet(Map<String, String> maps) {
        redisTemplate.opsForValue().multiSet(maps);
    }

    /**
     * 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在
     *
     * @param maps
     * @return 之前已经存在返回false, 不存在返回true
     */
    public boolean multiSetIfAbsent(Map<String, String> maps) {
        return redisTemplate.opsForValue().multiSetIfAbsent(maps);
    }

    /**
     * 增加(自增长), 负数则为自减
     *
     * @param key
     * @param
     * @return
     */
    public Long incrBy(String key, long increment) {
        return redisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * @param key
     * @param
     * @return
     */
    public Double incrByFloat(String key, double increment) {
        return redisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * 追加到末尾
     *
     * @param key
     * @param value
     * @return
     */
    public Integer append(String key, String value) {
        return redisTemplate.opsForValue().append(key, value);
    }

    /** -------------------hash相关操作------------------------- */

    /**
     * 获取存储在哈希表中指定字段的值
     *
     * @param key
     * @param field
     * @return
     */
    public Object hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 获取所有给定字段的值
     *
     * @param key
     * @return
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取所有给定字段的值
     *
     * @param key
     * @param fields
     * @return
     */
    public List<Object> hMultiGet(String key, Collection<Object> fields) {
        return redisTemplate.opsForHash().multiGet(key, fields);
    }

    public void hPut(String key, String hashKey, String value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public void hPutAll(String key, Map<String, String> maps) {
        redisTemplate.opsForHash().putAll(key, maps);
    }

    /**
     * 仅当hashKey不存在时才设置
     *
     * @param key
     * @param hashKey
     * @param value
     * @return
     */
    public Boolean hPutIfAbsent(String key, String hashKey, String value) {
        return redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * 删除一个或多个哈希表字段
     *
     * @param key
     * @param fields
     * @return
     */
    public Long hDelete(String key, Object... fields) {
        return redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 查看哈希表 key 中，指定的字段是否存在
     *
     * @param key
     * @param field
     * @return
     */
    public boolean hExists(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     *
     * @param key
     * @param field
     * @param increment
     * @return
     */
    public Long hIncrBy(String key, Object field, long increment) {
        return redisTemplate.opsForHash().increment(key, field, increment);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     *
     * @param key
     * @param field
     * @param delta
     * @return
     */
    public Double hIncrByFloat(String key, Object field, double delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }

    /**
     * 获取所有哈希表中的字段
     *
     * @param key
     * @return
     */
    public Set<Object> hKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取哈希表中字段的数量
     *
     * @param key
     * @return
     */
    public Long hSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 获取哈希表中所有值
     *
     * @param key
     * @return
     */
    public List<Object> hValues(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    /**
     * 迭代哈希表中的键值对
     *
     * @param key
     * @param options
     * @return
     */
    public Cursor<Entry<Object, Object>> hScan(String key, ScanOptions options) {
        return redisTemplate.opsForHash().scan(key, options);
    }

    /** ------------------------list相关操作---------------------------- */

    /**
     * 通过索引获取列表中的元素
     *
     * @param key
     * @param index
     * @return
     */
    public String lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 获取列表指定范围内的元素
     *
     * @param key
     * @param start 开始位置, 0是开始位置
     * @param end   结束位置, -1返回所有
     * @return
     */
    public List<String> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 存储在list头部
     *
     * @param key
     * @param value
     * @return
     */
    public Long lLeftPush(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public Long lLeftPushAll(String key, String... value) {
        return redisTemplate.opsForList().leftPushAll(key, value);
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public Long lLeftPushAll(String key, Collection<String> value) {
        return redisTemplate.opsForList().leftPushAll(key, value);
    }

    /**
     * 当list存在的时候才加入
     *
     * @param key
     * @param value
     * @return
     */
    public Long lLeftPushIfPresent(String key, String value) {
        return redisTemplate.opsForList().leftPushIfPresent(key, value);
    }

    /**
     * 如果pivot存在,再pivot前面添加
     *
     * @param key
     * @param pivot
     * @param value
     * @return
     */
    public Long lLeftPush(String key, String pivot, String value) {
        return redisTemplate.opsForList().leftPush(key, pivot, value);
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public Long lRightPush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public Long lRightPushAll(String key, String... value) {
        return redisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public Long lRightPushAll(String key, Collection<String> value) {
        return redisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * 为已存在的列表添加值
     *
     * @param key
     * @param value
     * @return
     */
    public Long lRightPushIfPresent(String key, String value) {
        return redisTemplate.opsForList().rightPushIfPresent(key, value);
    }

    /**
     * 在pivot元素的右边添加值
     *
     * @param key
     * @param pivot
     * @param value
     * @return
     */
    public Long lRightPush(String key, String pivot, String value) {
        return redisTemplate.opsForList().rightPush(key, pivot, value);
    }

    /**
     * 通过索引设置列表元素的值
     *
     * @param key
     * @param index 位置
     * @param value
     */
    public void lSet(String key, long index, String value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 移出并获取列表的第一个元素
     *
     * @param key
     * @return 删除的元素
     */
    public String lLeftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key
     * @param timeout 等待时间
     * @param unit    时间单位
     * @return
     */
    public String lBLeftPop(String key, long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().leftPop(key, timeout, unit);
    }

    /**
     * 移除并获取列表最后一个元素
     *
     * @param key
     * @return 删除的元素
     */
    public String lRightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key
     * @param timeout 等待时间
     * @param unit    时间单位
     * @return
     */
    public String lBRightPop(String key, long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().rightPop(key, timeout, unit);
    }

    /**
     * 移除列表的最后一个元素，并将该元素添加到另一个列表并返回
     *
     * @param sourceKey
     * @param destinationKey
     * @return
     */
    public String lRightPopAndLeftPush(String sourceKey, String destinationKey) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey,
                destinationKey);
    }

    /**
     * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param sourceKey
     * @param destinationKey
     * @param timeout
     * @param unit
     * @return
     */
    public String lBRightPopAndLeftPush(String sourceKey, String destinationKey,
                                        long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey,
                destinationKey, timeout, unit);
    }

    /**
     * 删除集合中值等于value得元素
     *
     * @param key
     * @param index index=0, 删除所有值等于value的元素; index>0, 从头部开始删除第一个值等于value的元素;
     *              index<0, 从尾部开始删除第一个值等于value的元素;
     * @param value
     * @return
     */
    public Long lRemove(String key, long index, String value) {
        return redisTemplate.opsForList().remove(key, index, value);
    }

    /**
     * 裁剪list
     *
     * @param key
     * @param start
     * @param end
     */
    public void lTrim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    /**
     * 获取列表长度
     *
     * @param key
     * @return
     */
    public Long lLen(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /** --------------------set相关操作-------------------------- */

    /**
     * set添加元素
     *
     * @param key
     * @param values
     * @return
     */
    public Long sAdd(String key, String... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * set移除元素
     *
     * @param key
     * @param values
     * @return
     */
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 移除并返回集合的一个随机元素
     *
     * @param key
     * @return
     */
    public String sPop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    /**
     * 将元素value从一个集合移到另一个集合
     *
     * @param key
     * @param value
     * @param destKey
     * @return
     */
    public Boolean sMove(String key, String value, String destKey) {
        return redisTemplate.opsForSet().move(key, value, destKey);
    }

    /**
     * 获取集合的大小
     *
     * @param key
     * @return
     */
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 判断集合是否包含value
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取两个集合的交集
     *
     * @param key
     * @param otherKey
     * @return
     */
    public Set<String> sIntersect(String key, String otherKey) {
        return redisTemplate.opsForSet().intersect(key, otherKey);
    }

    /**
     * 获取key集合与多个集合的交集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<String> sIntersect(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().intersect(key, otherKeys);
    }

    /**
     * key集合与otherKey集合的交集存储到destKey集合中
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long sIntersectAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(key, otherKey,
                destKey);
    }

    /**
     * key集合与多个集合的交集存储到destKey集合中
     *
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long sIntersectAndStore(String key, Collection<String> otherKeys,
                                   String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(key, otherKeys,
                destKey);
    }

    /**
     * 获取两个集合的并集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<String> sUnion(String key, String otherKeys) {
        return redisTemplate.opsForSet().union(key, otherKeys);
    }

    /**
     * 获取key集合与多个集合的并集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<String> sUnion(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().union(key, otherKeys);
    }

    /**
     * key集合与otherKey集合的并集存储到destKey中
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long sUnionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * key集合与多个集合的并集存储到destKey中
     *
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long sUnionAndStore(String key, Collection<String> otherKeys,
                               String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 获取两个集合的差集
     *
     * @param key
     * @param otherKey
     * @return
     */
    public Set<String> sDifference(String key, String otherKey) {
        return redisTemplate.opsForSet().difference(key, otherKey);
    }

    /**
     * 获取key集合与多个集合的差集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<String> sDifference(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().difference(key, otherKeys);
    }

    /**
     * key集合与otherKey集合的差集存储到destKey中
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long sDifference(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, otherKey,
                destKey);
    }

    /**
     * key集合与多个集合的差集存储到destKey中
     *
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long sDifference(String key, Collection<String> otherKeys,
                            String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, otherKeys,
                destKey);
    }

    /**
     * 获取集合所有元素
     *
     * @param key
     * @param
     * @param
     * @return
     */
    public Set<String> setMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 随机获取集合中的一个元素
     *
     * @param key
     * @return
     */
    public String sRandomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }

    /**
     * 随机获取集合中count个元素
     *
     * @param key
     * @param count
     * @return
     */
    public List<String> sRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     * 随机获取集合中count个元素并且去除重复的
     *
     * @param key
     * @param count
     * @return
     */
    public Set<String> sDistinctRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().distinctRandomMembers(key, count);
    }

    /**
     * @param key
     * @param options
     * @return
     */
    public Cursor<String> sScan(String key, ScanOptions options) {
        return redisTemplate.opsForSet().scan(key, options);
    }

    /**------------------zSet相关操作--------------------------------*/

    /**
     * 添加元素,有序集合是按照元素的score值由小到大排列
     *
     * @param key
     * @param value
     * @param score
     * @return
     */
    public Boolean zAdd(String key, String value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * @param key
     * @param values
     * @return
     */
    public Long zAdd(String key, Set<TypedTuple<String>> values) {
        return redisTemplate.opsForZSet().add(key, values);
    }

    /**
     * @param key
     * @param values
     * @return
     */
    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 增加元素的score值，并返回增加后的值
     *
     * @param key
     * @param value
     * @param delta
     * @return
     */
    public Double zIncrementScore(String key, String value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 返回元素在集合的排名,有序集合是按照元素的score值由小到大排列
     *
     * @param key
     * @param value
     * @return 0表示第一位
     */
    public Long zRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 返回元素在集合的排名,按元素的score值由大到小排列
     *
     * @param key
     * @param value
     * @return
     */
    public Long zReverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * 获取集合的元素, 从小到大排序
     *
     * @param key
     * @param start 开始位置
     * @param end   结束位置, -1查询所有
     * @return
     */
    public Set<String> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取集合元素, 并且把score值也获取
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<TypedTuple<String>> zRangeWithScores(String key, long start,
                                                    long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * 根据Score值查询集合元素
     *
     * @param key
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public Set<String> zRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 根据Score值查询集合元素, 从小到大排序
     *
     * @param key
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public Set<TypedTuple<String>> zRangeByScoreWithScores(String key,
                                                           double min, double max) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    public Set<TypedTuple<String>> zRangeByScoreWithScores(String key,
                                                           double min, double max, long start, long end) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max,
                start, end);
    }

    /**
     * 获取集合的元素, 从大到小排序
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<String> zReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 获取集合的元素, 从大到小排序, 并返回score值
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<TypedTuple<String>> zReverseRangeWithScores(String key,
                                                           long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start,
                end);
    }

    /**
     * 根据Score值查询集合元素, 从大到小排序
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<String> zReverseRangeByScore(String key, double min,
                                            double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    /**
     * 根据Score值查询集合元素, 从大到小排序
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<TypedTuple<String>> zReverseRangeByScoreWithScores(
            String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key,
                min, max);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    public Set<String> zReverseRangeByScore(String key, double min,
                                            double max, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max,
                start, end);
    }

    /**
     * 根据score值获取集合元素数量
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zCount(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * 获取集合大小
     *
     * @param key
     * @return
     */
    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 获取集合大小
     *
     * @param key
     * @return
     */
    public Long zZCard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 获取集合中value元素的score值
     *
     * @param key
     * @param value
     * @return
     */
    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 移除指定索引位置的成员
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Long zRemoveRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * 根据指定的score值的范围来移除成员
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zRemoveRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    /**
     * 获取key和otherKey的并集并存储在destKey中
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long zUnionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long zUnionAndStore(String key, Collection<String> otherKeys,
                               String destKey) {
        return redisTemplate.opsForZSet()
                .unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 交集
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long zIntersectAndStore(String key, String otherKey,
                                   String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key, otherKey,
                destKey);
    }

    /**
     * 交集
     *
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long zIntersectAndStore(String key, Collection<String> otherKeys,
                                   String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key, otherKeys,
                destKey);
    }

    /**
     * @param key
     * @param options
     * @return
     */
    public Cursor<TypedTuple<String>> zScan(String key, ScanOptions options) {
        return redisTemplate.opsForZSet().scan(key, options);
    }
}
