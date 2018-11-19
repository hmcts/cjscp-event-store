CREATE OR REPLACE FUNCTION update_pre_publish_queue()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$

BEGIN

IF (TG_OP = 'INSERT') THEN
  INSERT into pre_publish_queue(
    event_log_id,
    date_queued)
  values(NEW.id, timezone('UTC', now()));
ELSE
END IF;


RETURN NULL;
END;

$function$
