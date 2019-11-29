if redis.call('get',KEYS[1]) == ARGV[1] then
local result = redis.call('del',KEYS[1])
redis.call('publish',KEYS[2], ARGV[2])
return result
else
return 0
end